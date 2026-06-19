PV = "26.1.3"
SRC_URI[sha256sum] = "7725004e724b34c6d4fbaf5c48fc6c6223aa9f2741d6d7782c699b049356fc45"

SRC_URI:remove = "file://0001-freedreno-don-t-encode-build-path-into-binaries.patch"

FILESEXTRAPATHS:prepend := "${THISDIR}/mesa:"

SRC_URI += " \
	file://0001-intel-compiler-jay-avoid-C23-fixed-underlying-enum-t.patch \
"

PACKAGECONFIG[vdpau] = ""
