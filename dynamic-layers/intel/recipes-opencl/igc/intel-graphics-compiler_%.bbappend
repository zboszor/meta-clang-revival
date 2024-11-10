DEPENDS:remove = "clang clang-cross-x86_64"
DEPENDS:append = " clang15 clang15-cross-x86_64"

EXTRA_OECMAKE:remove = "-DIGC_OPTION__LLVM_PREFERRED_VERSION=${LLVMVERSION}"
EXTRA_OECMAKE:append = " -DIGC_OPTION__LLVM_PREFERRED_VERSION=${LLVM15VERSION}"

PV = "1.0.17791.16"
SRC_URI:remove = "git://github.com/intel/intel-graphics-compiler.git;protocol=https;name=igc;branch=releases/igc-1.0.17193"
SRC_URI:prepend = "git://github.com/intel/intel-graphics-compiler.git;protocol=https;name=igc;branch=releases/releases/igc-1.0.17791 "
SRCREV_igc = "ffa6fb4fc18ae047b8a8e91dbab83f8b3da5ce52"
SRCREV_vc = "8d2e809368443305155370573f3c6db8279ed87d"
FILESEXTRAPATHS:prepend := "${THISDIR}/files:"
