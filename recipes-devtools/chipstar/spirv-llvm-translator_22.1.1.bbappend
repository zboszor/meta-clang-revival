FILESEXTRAPATHS:prepend := "${THISDIR}/spirv-llvm-translator:"
SRC_URI += " \
	file://0002-coalesce-duplicate-phi-predecessors.patch \
"
