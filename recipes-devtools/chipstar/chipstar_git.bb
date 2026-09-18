require chipstar.inc

DEPENDS = " \
	chipstar-bin-native chrpath-native vim-native \
	llvm clang spirv-llvm-translator virtual/libopencl1 boost openmp \
	opencl-headers libffi libedit zlib libxml2 \
"

PACKAGECONFIG ??= ""
PACKAGECONFIG:x86-64 ??= "levelzero"

PACKAGECONFIG[levelzero] = ",,level-zero"

GCCVER = "15.3.0"

EXTRA_OECMAKE += " \
	-DCMAKE_BUILD_TYPE=Release \
	-DCMAKE_INSTALL_LIBDIR='${baselib}' \
	-DHIPCC_VERIFY=OFF \
	-DLLVM_DIR='${STAGING_LIBDIR}/cmake/llvm' \
	-DClang_DIR='${STAGING_LIBDIR}/cmake/clang' \
	-DCLANG_ROOT_PATH='${STAGING_EXECPREFIXDIR}' \
	-DCHIP_SET_RPATH=OFF \
	-DCHIP_SET_RPATH_OCL=OFF \
	-DCHIP_SET_RPATH_L0=OFF \
	-DFIND_LLVM_FROM_DEFAULT_PATHS=ON \
	-DCMAKE_CXX_COMPILER_PATH=${STAGING_BINDIR_NATIVE}/${TARGET_SYS}/${TARGET_PREFIX}clang++ \
	-DCMAKE_C_COMPILER_PATH=${STAGING_BINDIR_NATIVE}/${TARGET_SYS}/${TARGET_PREFIX}clang \
	-DPREPARE_BUILTINS=${STAGING_BINDIR_NATIVE}/prepare-builtins \
	-DHIP_USE_SYSROOT_OPTS_FOR_INSTALLED_HIPINFO=OFF \
	-DHIP_SYSROOT_OPTIONS='${@d.getVar("TOOLCHAIN_OPTIONS").strip()} --gcc-install-dir=${STAGING_LIBDIR}/${TARGET_SYS}/${GCCVER}' \
	-DHIP_SYSROOT_LINK_OPTIONS='${@d.getVar("TOOLCHAIN_OPTIONS").strip()} --gcc-install-dir=${STAGING_LIBDIR}/${TARGET_SYS}/${GCCVER}' \
	-DCHIP_BUILD_SAMPLES=ON \
	-DCHIP_BUILD_TESTS=ON \
"

OECMAKE_EXTRA_ROOT_PATH = "${S}/HIP/tests/catch/external"

# This causes a build problem in HIP tests
TUNE_CCARGS:remove:x86-64 = "-mfpmath=sse"

do_configure:prepend () {
	# This symlink, together with --gcc-install-dir= is needed
	# to prevent this issue:
	#
	# | .../chipstar/git/sources/chipstar-git/include/hip/devicelib/sync_and_util.hh:30:10: fatal error: 'cstddef' file not found
	# | 30 | #include <cstddef>
	# |          ^~~~~~~~~
	#
	# With "-v" added to CXXFLAGS, it turns out that the relative path
	# looked for by clang is not completely correct:
	#
	# | ignoring nonexistent directory ".../chipstar/git/recipe-sysroot/usr/lib/x86_64-oe-linux/15.3.0/../../../../x86_64-oe-linux/include"
	if [ ! -d ${RECIPE_SYSROOT}/${TARGET_SYS} ]; then
		mkdir ${RECIPE_SYSROOT}/${TARGET_SYS}
		ln -s ${STAGING_INCDIR} ${RECIPE_SYSROOT}/${TARGET_SYS}
		ln -s ${STAGING_LIBDIR} ${RECIPE_SYSROOT}/${TARGET_SYS}
	fi

	# Prevent TMPDIR references in libraries and headers
	sed -i \
		-e 's:@CHIP_SOURCE_DIR@:/usr/src/chipstar/git:' \
		-e 's:@CHIP_BUILD_DIR@:/usr/src/chipstar/git/build:' \
		-e 's:@LLVM_TOOLS_BINARY_DIR@:${bindir}:' \
		${S}/chipStarConfig.hh.in
}

do_compile:prepend () {
	cd ${B}

	# Build the binaries also built for native, plus the devicelib bitcode
	cmake_runcmake_build --target CHIP hipcc.bin hipconfig.bin LLVMHipPasses devicelib_bc

	# Save the target binaries
	mkdir -p ${B}/cross-${TARGET_ARCH}
	mv ${B}/bin/hipcc.bin ${B}/bin/hipconfig.bin ${B}/cross-${TARGET_ARCH}
	mv ${B}/lib/libLLVMHipSpvPasses.so ${B}/cross-${TARGET_ARCH}

	# Move the native binaries into the build
	cp ${STAGING_BINDIR_NATIVE}/hipcc ${B}/bin/hipcc.bin
	cp ${STAGING_BINDIR_NATIVE}/hipconfig ${B}/bin/hipconfig.bin
	cp ${STAGING_LIBDIR_NATIVE}/libLLVMHipSpvPasses.so ${B}/lib

	# Add wrappers for hipcc and hipconfig
	for i in hipcc hipconfig ; do
		if [ ! -f ${B}/bin/$i.real ]; then
			mv ${B}/bin/$i ${B}/bin/$i.real
			cat > "${B}/bin/$i" <<EOF
#!/bin/sh
export HIP_CLANG_PATH="${STAGING_BINDIR}"
export HIP_COMPILER_BIN=${STAGING_BINDIR_NATIVE}/${TARGET_SYS}/${TARGET_PREFIX}clang++
export HIP_LLC_BIN=${STAGING_BINDIR_NATIVE}/llc
export HIP_LLVM_CONFIG_BIN=${STAGING_BINDIR_CROSS}/llvm-config
export HIPCC_COMPILE_FLAGS_APPEND="--target=${TARGET_SYS} --sysroot=${RECIPE_SYSROOT} --gcc-install-dir=${STAGING_LIBDIR}/${TARGET_SYS}/${GCCVER}"
exec "${B}/bin/$i.real" $HIPCC_COMPILE_FLAGS_APPEND "\$@"
EOF
			chmod +x ${B}/bin/$i
		fi
	done
}

do_compile:append () {
	# Copy the target binaries back
	rm -f ${B}/bin/hipcc ${B}/bin/hipcc.bin
	mv ${B}/bin/hipcc.real  ${B}/bin/hipcc
	rm -f ${B}/bin/hipconfig ${B}/bin/hipconfig.bin
	mv ${B}/bin/hipconfig.real ${B}/bin/hipconfig
	rm -f ${B}/lib/libLLVMHipSpvPasses.so

	cp ${B}/cross-${TARGET_ARCH}/hipcc.bin ${B}/bin
	cp ${B}/cross-${TARGET_ARCH}/hipconfig.bin ${B}/bin
	cp ${B}/cross-${TARGET_ARCH}/libLLVMHipSpvPasses.so ${B}/lib
}

do_install:append () {
	# chipStar installs the complete set of OpenCL headers
	# which is provided by opencl-headers and opencl-clhpp
	# but it's a different version, which would cause an
	# LLVM build error complaining about OpenCL version mismatch.
	rm -rf ${D}${includedir}/CL

	# chipStar install a lot of cmake files for unit tests without
	# the sources. Remove them.
	rm -rf ${D}${datadir}/hip

	mv ${D}${prefix}/cmake/CHIP ${D}${libdir}/cmake
	rmdir ${D}${prefix}/cmake

	ln -s cucc ${D}${bindir}/nvcc

	install -m644 ${S}/samples/hip-cuda/RecursiveGaussian/RecursiveGaussian_Input.bmp ${D}${bindir}/chip_spv_samples/
	install -m644 ${S}/samples/cuda_samples/2_Graphics/dwtHaar1D/data/*.dat ${D}${bindir}/chip_spv_samples/cuda_samples/

	sed -i 's:^HIP_CLANG_PATH=.*$:HIP_CLANG_PATH=${bindir}:' ${D}${datadir}/.hipInfo

	sed -i \
		-e 's:\(INTERFACE_LINK_DIRECTORIES.*\)${STAGING_LIBDIR};${STAGING_LIBDIR}\(.*\)$:\1${libdir}\2:' \
		-e 's:\(INTERFACE_LINK_LIBRARIES.*\)${STAGING_LIBDIR}/libze_loader.so;${STAGING_LIBDIR}/libOpenCL.so\(.*\)$:\1libze_loader.so;libOpenCL.so\2:' \
		${D}${libdir}/cmake/hip/hip-targets.cmake \
		${D}${libdir}/cmake/CHIP/CHIPTargets.cmake
}

PACKAGES =+ "${PN}-lib ${PN}-samples"

FILES:${PN} += " \
	${bindir}/.hipVersion \
"

FILES:${PN}-lib = " \
	${datadir}/.hipInfo \
	${libdir}/libCHIP.so \
	${libdir}/libhiprtc.so \
	${libdir}/hip-device-lib/ \
	${libdir}/llvm/ \
"

FILES:${PN}-samples = " \
	${bindir}/chip_spv_samples/ \
"

TOOLCHAIN = "clang"
