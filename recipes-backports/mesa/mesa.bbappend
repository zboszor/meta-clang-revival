PV = "26.1.8"
SRC_URI[sha256sum] = "b320f65874fd9653ac6c0bd1616605387344e1247411a50c797b5f3fb9dc0b55"

SRC_URI:remove = "file://0001-freedreno-don-t-encode-build-path-into-binaries.patch"

FILESEXTRAPATHS:prepend := "${THISDIR}/mesa:"

SRC_URI += " \
	file://0001-intel-compiler-jay-avoid-C23-fixed-underlying-enum-t.patch \
"

PACKAGECONFIG[vdpau] = ""

# Use the mesa-libclc fork
PACKAGECONFIG[opencl] = "-Dgallium-rusticl=true -Dmesa-clc-bundle-headers=enabled, -Dgallium-rusticl=false, bindgen-cli-native clang mesa-libclc spirv-tools spirv-llvm-translator"

RDEPENDS:libopencl-mesa:remove = "${@bb.utils.contains('PACKAGECONFIG', 'opencl', 'libclc', '', d)}"
RDEPENDS:libopencl-mesa:append = "${@bb.utils.contains('PACKAGECONFIG', 'opencl', ' mesa-libclc', '', d)}"
