require opencl-clang.inc

SRCREV = "470cf0018e1ef6fc92eda1356f5f31f7da452abc"

BRANCH = "ocl-open-140"

LLVMTBLGENVER = "${LLVM14VERSION}"

EXTRA_OECMAKE += "-DPREFERRED_LLVM_VERSION=${LLVM14VERSION}"

DEPENDS += "clang14 spirv-llvm-translator-llvm14"
DEPENDS:append:class-target = " opencl-clang14-native"
