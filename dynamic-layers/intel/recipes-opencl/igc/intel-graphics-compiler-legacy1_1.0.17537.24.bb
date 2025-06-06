SUMMARY = "The Intel(R) Graphics Compiler for OpenCL(TM)"
DESCRIPTION = "The Intel(R) Graphics Compiler for OpenCL(TM) is an \
llvm based compiler for OpenCL(TM) targeting Intel Gen graphics \
hardware architecture."

LICENSE = "MIT & Apache-2.0"
LIC_FILES_CHKSUM = "file://IGC/BiFModule/Implementation/ExternalLibraries/libclc/LICENSE.TXT;md5=311cfc1a5b54bab8ed34a0b5fba4373e \
                    file://LICENSE.md;md5=488d74376edf2765f6e78d271543dde3 \
                    file://NOTICES.txt;md5=b81a52411c84df3419f20bad4d755880"

SRC_URI = "git://github.com/intel/intel-graphics-compiler.git;protocol=https;name=igc;branch=releases/igc-1.0.17537 \
           git://github.com/intel/vc-intrinsics.git;protocol=https;destsuffix=git/vc-intrinsics;name=vc;nobranch=1 \
           git://github.com/KhronosGroup/SPIRV-Tools.git;protocol=https;destsuffix=git/SPIRV-Tools;name=spirv-tools;branch=main \
           git://github.com/KhronosGroup/SPIRV-Headers.git;protocol=https;destsuffix=git/SPIRV-Headers;name=spirv-headers;branch=main \
           file://0003-Improve-Reproducibility-for-src-package.patch \
           file://0001-BiF-CMakeLists.txt-remove-opt-from-DEPENDS.patch \
           file://0001-external-SPIRV-Tools-change-path-to-tools-and-header.patch \
           "

SRC_URI:append:class-native = " file://0001-fix-tblgen.patch"

SRCREV_igc = "36a6b4e5190a181df613ab4cc6db5115ca8d56f8"
SRCREV_vc = "8d2e809368443305155370573f3c6db8279ed87d"
SRCREV_spirv-tools = "${SPIRV_TOOLS_SRCREV}"
SRCREV_spirv-headers = "${SPIRV_HEADERS_SRCREV}"

SRCREV_FORMAT = "igc_vc_spirv-tools_spirv-headers"

# Used to replace with relative path in reproducibility patch
export B

S = "${WORKDIR}/git"

inherit cmake pkgconfig qemu python3native

CXXFLAGS:append = " -Wno-error=nonnull"

COMPATIBLE_HOST = '(x86_64).*-linux'
COMPATIBLE_HOST:libc-musl = "null"

DEPENDS += " flex-native bison-native clang14 clang14-cross-x86_64 opencl-clang14 qemu-native python3-mako-native python3-pyyaml-native"

RDEPENDS:${PN} += "opencl-clang14"

PACKAGECONFIG ??= "vc"
PACKAGECONFIG[vc] = "-DIGC_BUILD__VC_ENABLED=ON -DIGC_OPTION__LINK_KHRONOS_SPIRV_TRANSLATOR=ON -DIGC_OPTION__SPIRV_TRANSLATOR_MODE=Prebuilds,-DIGC_BUILD__VC_ENABLED=OFF,"

EXTRA_OECMAKE = " \
                  -DIGC_OPTION__LLVM_PREFERRED_VERSION=${LLVM14VERSION} \
                  -DVC_INTRINSICS_SRC="${S}/vc-intrinsics" \
                  -DIGC_OPTION__LLVM_MODE=Prebuilds \
                  -DLLVM_TABLEGEN=${STAGING_BINDIR_NATIVE}/llvm-tblgen \
                  -DLLVM_LINK_EXE=${STAGING_BINDIR_NATIVE}/llvm-link \
                  -DCLANG_EXE=${STAGING_BINDIR_NATIVE}/clang \
                  -DCMAKE_CROSSCOMPILING_EMULATOR=${WORKDIR}/qemuwrapper \
                  "

do_configure:prepend:class-target () {
    # Write out a qemu wrapper that will be used by cmake.
    qemu_binary="${@qemu_wrapper_cmdline(d, d.getVar('STAGING_DIR_HOST'), [d.expand('${STAGING_DIR_HOST}${libdir}'),d.expand('${STAGING_DIR_HOST}${base_libdir}')])}"
    cat > ${WORKDIR}/qemuwrapper << EOF
#!/bin/sh
$qemu_binary "\$@"
EOF
    chmod +x ${WORKDIR}/qemuwrapper
}

UPSTREAM_CHECK_GITTAGREGEX = "^igc-(?P<pver>(?!19\..*)\d+(\.\d+)+)$"

PACKAGES =+ "${PN}-bin"

FILES:${PN}-bin = "${bindir}/*"

FILES:${PN} += " \
                 ${libdir}/igc/NOTICES.txt \
                 "

# libigc.so contains buildpaths
INSANE_SKIP:${PN} += "buildpaths"
