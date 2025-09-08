SUMMARY = "The Intel(R) Graphics Compute Runtime for OpenCL(TM)"
DESCRIPTION = "The Intel(R) Graphics Compute Runtime for OpenCL(TM) \
is an open source project to converge Intel's development efforts \
on OpenCL(TM) compute stacks supporting the GEN graphics hardware \
architecture."

LICENSE = "MIT & Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE.md;md5=eca6ec6997e18db166db7109cdbe611c \
                    file://third_party/opencl_headers/LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57"

SRC_URI = "git://github.com/intel/compute-runtime.git;protocol=https;branch=releases/24.35 \
           file://0001-intel-compute-runtime-fix-build-with-gcc13.patch \
           file://0002-Build-not-able-to-locate-cpp_generation_tool.patch \
           file://0003-external-ocloc.patch \
           file://0001-Fix-build-with-GCC-15.patch \
           "

SRCREV = "2cacae673fabf74bcd77ed8bece2ac3df9781f91"

DEPENDS += " intel-graphics-compiler-legacy1 gmmlib libva-initial qemu-native"

RDEPENDS:${PN} += " intel-graphics-compiler-legacy1 gmmlib"

inherit cmake pkgconfig qemu

COMPATIBLE_HOST = '(x86_64).*-linux'
COMPATIBLE_HOST:libc-musl = "null"

EXTRA_OECMAKE = " \
                 -DIGC_DIR=${STAGING_INCDIR}/igc \
                 -DBUILD_TYPE=Release \
                 -DSKIP_UNIT_TESTS=1 \
                 -DCCACHE_ALLOWED=FALSE \
                 -DNEO_DISABLE_LD_LLD=ON \
                 -DNEO_DISABLE_LD_GOLD=ON \
                 -DNEO_LEGACY_PLATFORMS_SUPPORT=ON \
                 -DNEO_CURRENT_PLATFORMS_SUPPORT=OFF \
                 -DNEO_FORCE_ENABLE_PLATFORMS_FOR_OCLOC=TRUE \
                 "

EXTRA_OECMAKE:append:class-target = " \
                                     -Docloc_cmd_prefix=ocloc \
                                     -DCMAKE_CROSSCOMPILING_EMULATOR=${WORKDIR}/qemuwrapper \
                                     "

PACKAGECONFIG ??= ""
PACKAGECONFIG:class-target:x86-64 = "levelzero"
PACKAGECONFIG[levelzero] = "-DBUILD_WITH_L0=ON, -DBUILD_WITH_L0=OFF, level-zero"

do_configure:prepend:class-target () {
    # Write out a qemu wrapper that will be used by cmake.
    qemu_binary="${@qemu_wrapper_cmdline(d, d.getVar('STAGING_DIR_HOST'), [d.expand('${B}/bin'),d.expand('${STAGING_DIR_HOST}${libdir}'),d.expand('${STAGING_DIR_HOST}${base_libdir}')])}"
    cat > ${WORKDIR}/qemuwrapper << EOF
#!/bin/sh
$qemu_binary "\$@"
EOF
    chmod +x ${WORKDIR}/qemuwrapper
}

FILES:${PN} += " \
                 ${libdir}/intel-opencl/libigdrcl_legacy1.so \
                 ${libdir}/libocloc_legacy1.so \
                 "

FILES:${PN}-dev = "${includedir}"

UPSTREAM_CHECK_GITTAGREGEX = "(?P<pver>\d+(\.\d+)+)"
