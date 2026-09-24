SUMMARY = "Mesa fork of libclc: Implementation of the library requirements of the OpenCL C programming language."
HOMEPAGE = "https://gitlab.freedesktop.org/karolherbst/mesa-libclc/"
SECTION = "devel"
LICENSE = "Apache-2.0-with-LLVM-exception"
LIC_FILES_CHKSUM = "file://LICENSE.TXT;md5=7cc795f6cbb2d801d84336b83c8017db"

SRC_URI = "git://gitlab.freedesktop.org/karolherbst/mesa-libclc.git;protocol=https;branch=llvm_21"

SRCREV = "7e18d1f0215c862f6d6aca5b31d6a2fd9e355066"

inherit cmake pkgconfig

# Depend explicitly on clang-native instead of using TOOLCHAIN as the build
# objects from this recipe are build explicitly using clang for GPU targets.
# We could INHIBIT_DEFAULT_DEPS to avoid any other toolchain but then we need
# to wrestle CMake to configure without a toolchain.
DEPENDS += "clang-native spirv-llvm-translator-native"

# Semicolon-separated list of targets to build
LIBCLC_TARGETS ?= "all"

EXTRA_OECMAKE += "-DLIBCLC_TARGETS_TO_BUILD='${LIBCLC_TARGETS}'"

FILES:${PN} += "${datadir}/mesa-clc"

BBCLASSEXTEND = "native nativesdk"
