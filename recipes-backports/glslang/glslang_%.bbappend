# Use the proper SRC_URI from oe-core master
SRC_URI = "git://github.com/KhronosGroup/glslang.git;protocol=https;branch=main;tag=vulkan-sdk-${PV} \
           file://0001-generate-glslang-pkg-config.patch \
           "

PV = "1.4.341"
SRCREV = "f0bd0257c308b9a26562c1a30c4748a0219cc951"
