PV = "26.1.5"
SRC_URI[sha256sum] = "79e421c7ce18cd9e790b8375920325779f10798630bf30e0b22f1a21c8617122"

SRC_URI:remove = "file://0001-freedreno-don-t-encode-build-path-into-binaries.patch"

FILESEXTRAPATHS:prepend := "${THISDIR}/mesa:"

SRC_URI += " \
	file://0001-intel-compiler-jay-avoid-C23-fixed-underlying-enum-t.patch \
"

PACKAGECONFIG[vdpau] = ""
