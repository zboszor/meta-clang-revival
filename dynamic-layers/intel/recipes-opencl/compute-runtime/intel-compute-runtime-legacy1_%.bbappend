# libva needs mesa which may be built with a different LLVM version
# that was used to build intel-graphics-compiler.
# This avoids pulling in the conflicting clang and clangNN versions
# into the same build.
DEPENDS:remove = "libva"
DEPENDS:append = " libva-initial"

PV = "24.35.30872.32"
PR = "r1"
SRC_URI:remove = "git://github.com/intel/compute-runtime.git;protocol=https;branch=releases/24.26"
SRC_URI:prepend = "git://github.com/intel/compute-runtime.git;protocol=https;branch=releases/24.35 "
SRCREV = "0361ca456469bed559af08e4f731c5b8754c76f1"
FILESEXTRAPATHS:prepend := "${THISDIR}/files:"
EXTRA_OECMAKE += " \
	-DNEO_LEGACY_PLATFORMS_SUPPORT=TRUE \
	-DNEO_CURRENT_PLATFORMS_SUPPORT=TRUE \
	-DNEO_FORCE_ENABLE_PLATFORMS_FOR_OCLOC=TRUE \
"

PACKAGECONFIG:class-target:x86-64 = "levelzero"
