PV = "26.1.1"
SRC_URI[sha256sum] = "8bd36c031cc6d0edfec04617527609454ee3a09ad53bdf983b45fc2c1e129b2e"

FILESEXTRAPATHS:prepend := "${THISDIR}/mesa:"

SRC_URI += " \
    file://0001-intel-compiler-jay-avoid-C23-fixed-underlying-enum-t.patch \
"
