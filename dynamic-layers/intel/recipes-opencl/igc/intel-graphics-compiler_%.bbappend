DEPENDS:remove = "clang clang-cross-x86_64 opencl-clang"
DEPENDS:append = " clang15 clang15-cross-x86_64 opencl-clang15"

RDEPENDS:${PN}:remove = "opencl-clang"
RDEPENDS:${PN}:append = " opencl-clang15"

SRCREV_spirv-tools = "${SPIRV_TOOLS_SRCREV}"
SRCREV_spirv-headers = "${SPIRV_HEADERS_SRCREV}"

EXTRA_OECMAKE:remove = " \
	-DIGC_OPTION__LLVM_PREFERRED_VERSION=${LLVMVERSION} \
	-DLLVM_TABLEGEN=${STAGING_BINDIR_NATIVE}/llvm-tblgen \
"
EXTRA_OECMAKE:append = " \
	-DIGC_OPTION__LLVM_PREFERRED_VERSION=${LLVM15VERSION} \
	-DLLVM_TABLEGEN=${STAGING_BINDIR_NATIVE}/llvm-tblgen${LLVM15VERSION} \
"

PACKAGES =+ "${PN}-bin"

FILES:${PN}-bin = "${bindir}/*"
