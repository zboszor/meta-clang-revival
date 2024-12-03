LICENSE = "NCSA"
LIC_FILES_CHKSUM = "file://LICENSE.TXT;md5=47e311aa9caedd1b3abf098bd7814d1d"

BRANCH = "llvm_release_170"
SRC_URI = "git://github.com/KhronosGroup/SPIRV-LLVM-Translator;protocol=https;branch=${BRANCH}"

PV = "17.0.7"
SRCREV = "6542fe74f96ee10aa9ef5267428267b4b60735cf"

S = "${WORKDIR}/git"

DEPENDS = "spirv-headers spirv-tools spirv-tools-native clang17"

inherit cmake pkgconfig python3native

# Specify any options you want to pass to cmake using EXTRA_OECMAKE:
EXTRA_OECMAKE = "\
        -DBASE_LLVM_VERSION=${LLVM17VERSION} \
        -DBUILD_SHARED_LIBS=ON \
        -DLLVM_LINK_LLVM_DYLIB=ON \
        -DCMAKE_BUILD_TYPE=Release \
        -DCMAKE_POSITION_INDEPENDENT_CODE=ON \
        -DCMAKE_SKIP_RPATH=ON \
        -DLLVM_EXTERNAL_LIT=lit \
        -DLLVM_INCLUDE_TESTS=ON \
        -Wno-dev \
        -DCCACHE_ALLOWED=FALSE \
"

PACKAGES =+ "${PN}-bin"
FILES:${PN}-bin = "${bindir}/*"

BBCLASSEXTEND = "native nativesdk"

EXCLUDE_FROM_WORLD = "1"
