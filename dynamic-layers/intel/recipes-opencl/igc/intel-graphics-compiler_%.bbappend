DEPENDS:remove = "clang clang-cross-x86_64 opencl-clang"
DEPENDS:append = " clang14 clang14-cross-x86_64 opencl-clang14"

RDEPENDS:${PN}:remove = "opencl-clang"
RDEPENDS:${PN}:append = " opencl-clang14"

EXTRA_OECMAKE:remove = "-DIGC_OPTION__LLVM_PREFERRED_VERSION=${LLVMVERSION}"
EXTRA_OECMAKE:append = " -DIGC_OPTION__LLVM_PREFERRED_VERSION=${LLVM14VERSION}"

PV = "1.0.17537.24"
PR = "r1"
SRC_URI:remove = "git://github.com/intel/intel-graphics-compiler.git;protocol=https;name=igc;branch=releases/igc-1.0.17193"
SRC_URI:prepend = "git://github.com/intel/intel-graphics-compiler.git;protocol=https;name=igc;branch=releases/igc-1.0.17537 "
SRCREV_igc = "36a6b4e5190a181df613ab4cc6db5115ca8d56f8"
SRCREV_vc = "8d2e809368443305155370573f3c6db8279ed87d"
SRCREV_spirv-tools = "${SPIRV_TOOLS_SRCREV}"
SRCREV_spirv-headers = "${SPIRV_HEADERS_SRCREV}"
FILESEXTRAPATHS:prepend := "${THISDIR}/files:"
DEPENDS += "python3-pyyaml-native"
