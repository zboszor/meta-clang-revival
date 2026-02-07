PV = "4.20.3"
SRC_URI[archive.sha256sum] = "2873f2903088a66c71173ea2ed85ffae266a66b972c3a4842bbb2f6f187ec153"
SRC_URI[sha256sum] = "2873f2903088a66c71173ea2ed85ffae266a66b972c3a4842bbb2f6f187ec153"

# To fix a build issue under Yocto 5.3
FILES:${PN}:append = " ${datadir}/bash-completion"

# GTK4 4.20.x has the print backends built-in instead of separate shared objects
PACKAGECONFIG[cups] = "-Dprint-cups=enabled,-Dprint-cups=disabled,cups,cups"
