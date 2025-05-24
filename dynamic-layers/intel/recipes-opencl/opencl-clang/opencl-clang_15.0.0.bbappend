PV = "20.1.0"
BRANCH = "ocl-open-200"
SRCREV = "24c2c66279e3cf01544f727bf726e2e294f299b3"
SRC_URI:remove = "file://0002-Request-native-clang-only-when-cross-compiling-464.patch"
LICENSE = "Apache-2.0-with-LLVM-exception"
LIC_FILES_CHKSUM = "file://LICENSE;md5=8a15a0759ef07f2682d2ba4b893c9afe"

def llvm_major(d):
    llvm_ver = d.getVar('LLVMVERSION')
    return llvm_ver.split('.')[0]

do_configure:prepend:class-target () {
	# /usr/lib/clang/<major>/include and
	# /usr/lib/clang/<fullversion>/lib directories are split,
	# probably intentionally. Still, under Yocto, it's guaranteed
	# that there's a single version. Make /usr/lib/clang/<major>
	# a symlink to /usr/lib/clang/<fullversion>. This should actually
	# be in the LLVM or CLANG recipes but it's faster to test here.
	if [[ ! -h ${STAGING_LIBDIR}/clang/${@llvm_major(d)} ]]; then
		if [[ -d ${STAGING_LIBDIR}/clang/${@llvm_major(d)} ]]; then
			mkdir -p ${STAGING_LIBDIR}/clang/${LLVMVERSION}
			mv ${STAGING_LIBDIR}/clang/${@llvm_major(d)}/* ${STAGING_LIBDIR}/clang/${LLVMVERSION}/
			rmdir ${STAGING_LIBDIR}/clang/${@llvm_major(d)}
			ln -s ${LLVMVERSION} ${STAGING_LIBDIR}/clang/${@llvm_major(d)}
		fi
	fi
}
