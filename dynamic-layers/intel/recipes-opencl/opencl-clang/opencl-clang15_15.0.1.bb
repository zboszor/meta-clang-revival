require opencl-clang.inc

SRCREV = "58242977b4092cf5eb94a10dd144691c12c87001"

BRANCH = "ocl-open-150"

EXTRA_OECMAKE += "-DPREFERRED_LLVM_VERSION=${LLVM15VERSION}"

DEPENDS += "clang15 spirv-llvm-translator-llvm15"
DEPENDS:append:class-target = " opencl-clang15-native"
