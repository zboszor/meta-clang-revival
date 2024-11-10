# libva needs mesa which may be built with a different LLVM version
# that was used to build intel-graphics-compiler.
# This avoids pulling in the conflicting clang and clangNN versions
# into the same build.
DEPENDS:remove = "libva"
DEPENDS:append = " libva-initial"

PV = "24.39.31294.21"
SRC_URI:remove = "git://github.com/intel/compute-runtime.git;protocol=https;branch=releases/24.26"
SRC_URI:prepend = "git://github.com/intel/compute-runtime.git;protocol=https;branch=releases/24.39"
SRCREV = "92c9efbaf0e2c2babc34d1b4df3e7e317851ea1b"
FILESEXTRAPATHS:prepend := "${THISDIR}/files:"
EXTRA_OECMAKE += "-DNEO_ALLOW_LEGACY_PLATFORMS_SUPPORT=ON"
