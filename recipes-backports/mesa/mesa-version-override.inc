PV = "26.2.1"
SRC_URI[sha256sum] = "c47e81bddc4760360a41ac3c5acec38acb81f9d750ecef47e7f3adc7021a4442"

SRC_URI:remove = "file://0001-gallivm-Fix-armhf-build-against-LLVM-22.patch"

FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

# Use the mesa-libclc fork
PACKAGECONFIG[opencl] = "-Dgallium-rusticl=true -Dmesa-clc-bundle-headers=enabled, -Dgallium-rusticl=false, bindgen-cli-native clang mesa-libclc spirv-tools spirv-llvm-translator"

RDEPENDS:libopencl-mesa:remove = "${@bb.utils.contains('PACKAGECONFIG', 'opencl', 'libclc', '', d)}"
RDEPENDS:libopencl-mesa:append = "${@bb.utils.contains('PACKAGECONFIG', 'opencl', ' mesa-libclc', '', d)}"
