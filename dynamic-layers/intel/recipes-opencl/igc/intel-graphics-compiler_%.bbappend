DEPENDS:remove = "clang clang-cross-x86_64 opencl-clang"
DEPENDS:append = " clang15 clang15-cross-x86_64 opencl-clang15"

RDEPENDS:${PN}:remove = "opencl-clang"
RDEPENDS:${PN}:append = " opencl-clang15"

EXTRA_OECMAKE:remove = "-DIGC_OPTION__LLVM_PREFERRED_VERSION=${LLVMVERSION}"
EXTRA_OECMAKE:append = " -DIGC_OPTION__LLVM_PREFERRED_VERSION=${LLVM15VERSION}"

PACKAGES =+ "${PN}-bin"

FILES:${PN}-bin = "${bindir}/*"
