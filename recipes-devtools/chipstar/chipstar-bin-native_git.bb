inherit native

require chipstar.inc

DEPENDS = " \
	llvm-native clang-native vim-native spirv-llvm-translator-native \
	chrpath-native virtual/libopencl1-native opencl-headers-native \
	libffi-native libedit-native zlib-native libxml2-native \
"

OECMAKE_TARGET_COMPILE = "hipcc.bin hipconfig.bin LLVMHipPasses prepare-builtins"

EXTRA_OECMAKE += " \
	-DCMAKE_BUILD_TYPE=Release \
	-DCMAKE_INSTALL_LIBDIR='${baselib}' \
	-DCHIP_SET_RPATH=OFF \
	-DCHIP_SET_RPATH_OCL=OFF \
	-DCHIP_SET_RPATH_L0=OFF \
	-DCHIP_BUILD_SAMPLES=OFF \
	-DCHIP_BUILD_TESTS=OFF \
"

do_install () {
	install -d -m755 ${D}${bindir}
	install -m755 ${B}/bin/hipcc.bin ${D}${bindir}/hipcc
	install -m755 ${B}/bin/hipconfig.bin ${D}${bindir}/hipconfig
	install -m755 ${B}/bitcode/ROCm-Device-Libs/utils/prepare-builtins/prepare-builtins ${D}${bindir}
	install -m755 ${B}/bin/cucc ${D}${bindir}
	ln -s cucc ${D}${bindir}/nvcc

	install -d -m755 ${D}${libdir}
	install -m755 ${B}/lib/libLLVMHipSpvPasses.so ${D}${libdir}

	install -d -m755 ${D}${datadir}
	install -m644 ${B}/share/.hipInfo_install ${D}${datadir}/.hipInfo
	sed -i \
		-e 's:--target=[^ ]*:--target=@TARGET_SYS@:g' \
		-e 's:${STAGING_BINDIR}:@STAGING_BINDIR@:g' \
		-e 's:${STAGING_INCDIR}:@STAGING_INCDIR@:g' \
		-e 's:${STAGING_LIBDIR}:@STAGING_LIBDIR@:g' \
		-e 's:${STAGING_EXECPREFIXDIR}:@STAGING_EXECPREFIXDIR@:g' \
		${D}${datadir}/.hipInfo
}
