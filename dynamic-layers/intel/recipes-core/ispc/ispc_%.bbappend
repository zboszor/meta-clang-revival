DEPENDS:remove = "clang-native"
DEPENDS:append = " clang15-native"
DEPENDS:remove:class-target = "clang"
DEPENDS:append:class-target = " clang15"

# ispc-dev will omit clang15-dev and others this way
RRECOMMENDS:ispc-dev[nodeprrecs] = "1"
