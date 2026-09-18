DESCRIPTION = "chipStar enables compiling and running HIP and \
CUDA applications on platforms which support SPIR-V as the device \
intermediate representation. It supports OpenCL and Level Zero as \
the low-level runtime alternatives."
HOMEPAGE = "https://github.com/CHIP-SPV/chipStar.git"
LICENSE = "MIT"

SRCREV = "7879e2acd63a7d1123a9220cf17fa6e05420c8e4"
LIC_FILES_CHKSUM = "file://LICENSE;md5=18c85a1e3dc7c297bc0ab9ed498ceb21"

SRC_URI = " \
	gitsm://github.com/CHIP-SPV/chipStar.git;protocol=https;branch=main \
"

inherit cmake
