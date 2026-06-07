PV = "26.1.2"
SRC_URI[sha256sum] = "bac2bca9121897a2b8162e79636b50ac998fca799c8e6cf914edd85962babdf0"

FILESEXTRAPATHS:prepend := "${THISDIR}/mesa:"

SRC_URI += " \
	file://0001-intel-compiler-jay-avoid-C23-fixed-underlying-enum-t.patch \
	file://41991.patch \
"
