PV = "26.1.0"
SRC_URI[sha256sum] = "a5095e6dc2986c78f0cef4c5555dc803e93b6bfe5670e991f9e8bd49395bae19"

FILESEXTRAPATHS:prepend := "${THISDIR}/mesa:"

SRC_URI += " \
    file://0001-intel-compiler-jay-avoid-C23-fixed-underlying-enum-t.patch \
"
