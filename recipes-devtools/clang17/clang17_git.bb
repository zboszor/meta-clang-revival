# Copyright (C) 2014 Khem Raj <raj.khem@gmail.com>
# Released under the MIT license (see COPYING.MIT for the terms)

DESCRIPTION = "LLVM based C/C++ compiler"
HOMEPAGE = "http://clang.llvm.org/"
SECTION = "devel"

require clang.inc
require common-source.inc

INHIBIT_DEFAULT_DEPS = "1"

BUILD_CC:class-nativesdk = "clang"
BUILD_CXX:class-nativesdk = "clang++"
BUILD_AR:class-nativesdk = "llvm-ar"
BUILD_RANLIB:class-nativesdk = "llvm-ranlib"
BUILD_NM:class-nativesdk = "llvm-nm"
LDFLAGS:remove:class-nativesdk = "-fuse-ld=lld"

LDFLAGS:append:class-target:riscv32 = " -Wl,--no-as-needed -latomic -Wl,--as-needed"
LDFLAGS:append:class-target:mips = " -Wl,--no-as-needed -latomic -Wl,--as-needed"

inherit cmake cmake-native pkgconfig python3native python3targetconfig

OECMAKE_FIND_ROOT_PATH_MODE_PROGRAM = "BOTH"

def get_clang_experimental_arch(bb, d, arch_var):
    import re
    a = d.getVar(arch_var, True)
    return ""

def get_clang_arch(bb, d, arch_var):
    import re
    a = d.getVar(arch_var, True)
    if   re.match('(i.86|athlon|x86.64)$', a):         return 'X86'
    elif re.match('arm$', a):                          return 'ARM'
    elif re.match('armeb$', a):                        return 'ARM'
    elif re.match('aarch64$', a):                      return 'AArch64'
    elif re.match('aarch64_be$', a):                   return 'AArch64'
    elif re.match('mips(isa|)(32|64|)(r6|)(el|)$', a): return 'Mips'
    elif re.match('riscv32$', a):                      return 'riscv32'
    elif re.match('riscv64$', a):                      return 'riscv64'
    elif re.match('p(pc|owerpc)(|64)', a):             return 'PowerPC'
    elif re.match('loongarch64$', a):                  return 'loongarch64'
    else:
        bb.note("'%s' is not a primary llvm architecture" % a)
    return ""

def get_clang_host_arch(bb, d):
    return get_clang_arch(bb, d, 'HOST_ARCH')

def get_clang_target_arch(bb, d):
    return get_clang_arch(bb, d, 'TARGET_ARCH')

def get_clang_experimental_target_arch(bb, d):
    return get_clang_experimental_arch(bb, d, 'TARGET_ARCH')

PACKAGECONFIG_CLANG_COMMON = "eh libedit rtti shared-libs \
                              ${@bb.utils.contains('TC_CXX_RUNTIME', 'llvm', 'compiler-rt libcplusplus libomp unwindlib', '', d)} \
                              "

PACKAGECONFIG ??= "compiler-rt libcplusplus lldb-wchar terminfo \
                   ${PACKAGECONFIG_CLANG_COMMON} \
                   ${@bb.utils.filter('DISTRO_FEATURES', 'lto thin-lto', d)} \
                   "
PACKAGECONFIG:class-native = "clangd \
                              ${PACKAGECONFIG_CLANG_COMMON} \
                              "
PACKAGECONFIG:class-nativesdk = "clangd \
                                 ${PACKAGECONFIG_CLANG_COMMON} \
                                 ${@bb.utils.filter('DISTRO_FEATURES', 'lto thin-lto', d)} \
                                 "

PACKAGECONFIG[bootstrap] = "-DCLANG_ENABLE_BOOTSTRAP=On -DCLANG_BOOTSTRAP_PASSTHROUGH='${PASSTHROUGH}' -DBOOTSTRAP_LLVM_ENABLE_LTO=Thin -DBOOTSTRAP_LLVM_ENABLE_LLD=ON,,,"
PACKAGECONFIG[clangd] = "-DCLANG_ENABLE_CLANGD=ON,-DCLANG_ENABLE_CLANGD=OFF,,"
PACKAGECONFIG[compiler-rt] = "-DCLANG_DEFAULT_RTLIB=compiler-rt,,"
PACKAGECONFIG[eh] = "-DLLVM_ENABLE_EH=ON,-DLLVM_ENABLE_EH=OFF,,"
PACKAGECONFIG[libcplusplus] = "-DCLANG_DEFAULT_CXX_STDLIB=libc++,,"
PACKAGECONFIG[libedit] = "-DLLVM_ENABLE_LIBEDIT=ON -DLLDB_ENABLE_LIBEDIT=ON,-DLLVM_ENABLE_LIBEDIT=OFF -DLLDB_ENABLE_LIBEDIT=OFF,libedit libedit-native"
PACKAGECONFIG[libomp] = "-DCLANG_DEFAULT_OPENMP_RUNTIME=libomp,,"
PACKAGECONFIG[lldb-lua] = "-DLLDB_ENABLE_LUA=ON,-DLLDB_ENABLE_LUA=OFF,lua"
PACKAGECONFIG[lldb-wchar] = "-DLLDB_EDITLINE_USE_WCHAR=1,-DLLDB_EDITLINE_USE_WCHAR=0,"
PACKAGECONFIG[lto] = "-DLLVM_ENABLE_LTO=Full -DLLVM_BINUTILS_INCDIR=${STAGING_INCDIR},,binutils,"
PACKAGECONFIG[pfm] = "-DLLVM_ENABLE_LIBPFM=ON,-DLLVM_ENABLE_LIBPFM=OFF,libpfm,"
PACKAGECONFIG[rtti] = "-DLLVM_ENABLE_RTTI=ON,-DLLVM_ENABLE_RTTI=OFF,,"
PACKAGECONFIG[shared-libs] = "-DLLVM_BUILD_LLVM_DYLIB=ON -DLLVM_LINK_LLVM_DYLIB=ON,,,"
PACKAGECONFIG[split-dwarf] = "-DLLVM_USE_SPLIT_DWARF=ON,-DLLVM_USE_SPLIT_DWARF=OFF,,"
PACKAGECONFIG[terminfo] = "-DLLVM_ENABLE_TERMINFO=ON -DCOMPILER_RT_TERMINFO_LIB=ON,-DLLVM_ENABLE_TERMINFO=OFF -DCOMPILER_RT_TERMINFO_LIB=OFF,ncurses,"
PACKAGECONFIG[thin-lto] = "-DLLVM_ENABLE_LTO=Thin -DLLVM_BINUTILS_INCDIR=${STAGING_INCDIR},,binutils,"
PACKAGECONFIG[unwindlib] = "-DCLANG_DEFAULT_UNWINDLIB=libunwind,-DCLANG_DEFAULT_UNWINDLIB=libgcc,,"

OECMAKE_SOURCEPATH = "${S}/llvm"

OECMAKE_TARGET_COMPILE = "${@bb.utils.contains('PACKAGECONFIG', 'bootstrap', 'stage2', 'all', d)}"
OECMAKE_TARGET_INSTALL = "${@bb.utils.contains('PACKAGECONFIG', 'bootstrap', 'stage2-install', 'install', d)}"
BINPATHPREFIX = "${@bb.utils.contains('PACKAGECONFIG', 'bootstrap', '/tools/clang/stage2-bins/NATIVE', '', d)}"

PASSTHROUGH = "\
CLANG_DEFAULT_RTLIB;CLANG_DEFAULT_CXX_STDLIB;LLVM_BUILD_LLVM_DYLIB;LLVM_LINK_LLVM_DYLIB;\
LLVM_ENABLE_ASSERTIONS;LLVM_ENABLE_EXPENSIVE_CHECKS;LLVM_ENABLE_PIC;\
LLVM_BINDINGS_LIST;LLVM_ENABLE_FFI;FFI_INCLUDE_DIR;LLVM_OPTIMIZED_TABLEGEN;\
LLVM_ENABLE_RTTI;LLVM_ENABLE_EH;LLVM_BUILD_EXTERNAL_COMPILER_RT;CMAKE_SYSTEM_NAME;\
CMAKE_BUILD_TYPE;BUILD_SHARED_LIBS;LLVM_ENABLE_PROJECTS;LLVM_BINUTILS_INCDIR;\
LLVM_TARGETS_TO_BUILD;LLVM_EXPERIMENTAL_TARGETS_TO_BUILD;PYTHON_EXECUTABLE;\
PYTHON_LIBRARY;PYTHON_INCLUDE_DIR;LLVM_TEMPORARILY_ALLOW_OLD_TOOLCHAIN;LLDB_EDITLINE_USE_WCHAR;\
LLVM_ENABLE_LIBEDIT;LLDB_ENABLE_LIBEDIT;LLDB_PYTHON_RELATIVE_PATH;LLDB_PYTHON_EXE_RELATIVE_PATH;\
LLDB_PYTHON_EXT_SUFFIX;CMAKE_C_FLAGS_RELEASE;CMAKE_CXX_FLAGS_RELEASE;CMAKE_ASM_FLAGS_RELEASE;\
CLANG_DEFAULT_CXX_STDLIB;CLANG_DEFAULT_RTLIB;CLANG_DEFAULT_UNWINDLIB;\
CLANG_DEFAULT_OPENMP_RUNTIME;LLVM_ENABLE_PER_TARGET_RUNTIME_DIR;\
LLVM_BUILD_TOOLS;LLVM_USE_HOST_TOOLS;LLVM_CONFIG_PATH;\
"
#
# Default to build all OE-Core supported target arches (user overridable).
# Gennerally setting LLVM_TARGETS_TO_BUILD = "" in local.conf is ok in most simple situations
# where only one target architecture is needed along with just one build arch (usually X86)
#
LLVM_TARGETS_TO_BUILD ?= "AMDGPU;AArch64;ARM;BPF;Mips;PowerPC;RISCV;X86;LoongArch"

LLVM_EXPERIMENTAL_TARGETS_TO_BUILD ?= ""
LLVM_EXPERIMENTAL_TARGETS_TO_BUILD:append = ";${@get_clang_experimental_target_arch(bb, d)}"

HF = ""
HF:class-target = "${@ bb.utils.contains('TUNE_CCARGS_MFLOAT', 'hard', 'hf', '', d)}"
HF[vardepvalue] = "${HF}"

LLVM_PROJECTS ?= "clang;clang-tools-extra;lld${LLDB}"
LLDB ?= ";lldb"
# LLDB support for RISCV/Mips32 does not work yet
LLDB:riscv32 = ""
LLDB:riscv64 = ""
LLDB:mips = ""
LLDB:mipsel = ""
LLDB:powerpc = ""

# linux hosts (.so) on Windows .pyd
SOLIBSDEV:mingw32 = ".pyd"

#CMAKE_VERBOSE = "VERBOSE=1"

EXTRA_OECMAKE += "-DLLVM_ENABLE_ASSERTIONS=OFF \
                  -DLLVM_ENABLE_PER_TARGET_RUNTIME_DIR=OFF \
                  -DLLVM_ENABLE_EXPENSIVE_CHECKS=OFF \
                  -DLLVM_ENABLE_PIC=ON \
                  -DCLANG_DEFAULT_PIE_ON_LINUX=ON \
                  -DLLVM_BINDINGS_LIST='' \
                  -DLLVM_ENABLE_FFI=ON \
                  -DLLVM_ENABLE_ZSTD=ON \
                  -DFFI_INCLUDE_DIR=$(pkg-config --variable=includedir libffi) \
                  -DLLVM_OPTIMIZED_TABLEGEN=ON \
                  -DLLVM_BUILD_EXTERNAL_COMPILER_RT=ON \
                  -DCMAKE_SYSTEM_NAME=Linux \
                  -DCMAKE_BUILD_TYPE=Release \
                  -DCMAKE_CXX_FLAGS_RELEASE='${CXXFLAGS} -DNDEBUG -g0' \
                  -DCMAKE_C_FLAGS_RELEASE='${CFLAGS} -DNDEBUG -g0' \
                  -DBUILD_SHARED_LIBS=OFF \
                  -DLLVM_ENABLE_PROJECTS='${LLVM_PROJECTS}' \
                  -DLLVM_BINUTILS_INCDIR=${STAGING_INCDIR} \
                  -DLLVM_TEMPORARILY_ALLOW_OLD_TOOLCHAIN=ON \
                  -DLLVM_TARGETS_TO_BUILD='${LLVM_TARGETS_TO_BUILD}' \
                  -DLLVM_EXPERIMENTAL_TARGETS_TO_BUILD='${LLVM_EXPERIMENTAL_TARGETS_TO_BUILD}' \
"

EXTRA_OECMAKE:append:class-native = "\
                  -DPYTHON_EXECUTABLE='${PYTHON}' \
"
EXTRA_OECMAKE:append:class-nativesdk = "\
                  -DCROSS_TOOLCHAIN_FLAGS_NATIVE='-DLLDB_PYTHON_RELATIVE_PATH=${PYTHON_SITEPACKAGES_DIR} \
                                                  -DLLDB_PYTHON_EXE_RELATIVE_PATH=${PYTHON} \
                                                  -DLLDB_PYTHON_EXT_SUFFIX=${SOLIBSDEV} \
                                                  -DCMAKE_TOOLCHAIN_FILE=${WORKDIR}/toolchain-native.cmake' \
                  -DCMAKE_RANLIB=${STAGING_BINDIR_TOOLCHAIN}/${TARGET_PREFIX}llvm-ranlib \
                  -DCMAKE_AR=${STAGING_BINDIR_TOOLCHAIN}/${TARGET_PREFIX}llvm-ar \
                  -DCMAKE_NM=${STAGING_BINDIR_TOOLCHAIN}/${TARGET_PREFIX}llvm-nm \
                  -DCMAKE_STRIP=${STAGING_BINDIR_TOOLCHAIN}/${TARGET_PREFIX}llvm-strip \
                  -DLLVM_NATIVE_TOOL_DIR=${STAGING_BINDIR_NATIVE} \
                  -DLLVM_HEADERS_TABLEGEN=${STAGING_BINDIR_NATIVE}/llvm-min-tblgen \
                  -DPYTHON_LIBRARY=${STAGING_LIBDIR}/lib${PYTHON_DIR}${PYTHON_ABI}.so \
                  -DLLDB_PYTHON_RELATIVE_PATH=${PYTHON_SITEPACKAGES_DIR} \
                  -DLLDB_PYTHON_EXE_RELATIVE_PATH=${PYTHON} \
                  -DLLDB_PYTHON_EXT_SUFFIX=${SOLIBSDEV} \
                  -DPYTHON_INCLUDE_DIR=${STAGING_INCDIR}/${PYTHON_DIR}${PYTHON_ABI} \
                  -DPYTHON_EXECUTABLE='${PYTHON}' \
"
EXTRA_OECMAKE:append:class-target = "\
                  -DLLVM_NATIVE_TOOL_DIR=${STAGING_BINDIR_NATIVE} \
                  -DLLVM_HEADERS_TABLEGEN=${STAGING_BINDIR_NATIVE}/llvm-min-tblgen \
                  -DCMAKE_RANLIB=${STAGING_BINDIR_TOOLCHAIN}/${TARGET_PREFIX}llvm-ranlib \
                  -DCMAKE_AR=${STAGING_BINDIR_TOOLCHAIN}/${TARGET_PREFIX}llvm-ar \
                  -DCMAKE_NM=${STAGING_BINDIR_TOOLCHAIN}/${TARGET_PREFIX}llvm-nm \
                  -DCMAKE_STRIP=${STAGING_BINDIR_TOOLCHAIN}/${TARGET_PREFIX}llvm-strip \
                  -DLLVM_TARGET_ARCH=${@get_clang_target_arch(bb, d)} \
                  -DLLVM_DEFAULT_TARGET_TRIPLE=${TARGET_SYS}${HF} \
                  -DLLVM_HOST_TRIPLE=${TARGET_SYS}${HF} \
                  -DPYTHON_LIBRARY=${STAGING_LIBDIR}/lib${PYTHON_DIR}${PYTHON_ABI}.so \
                  -DPYTHON_INCLUDE_DIR=${STAGING_INCDIR}/${PYTHON_DIR}${PYTHON_ABI} \
                  -DLLVM_LIBDIR_SUFFIX=${@d.getVar('baselib').replace('lib', '')} \
                  -DLLDB_PYTHON_RELATIVE_PATH=${PYTHON_SITEPACKAGES_DIR} \
                  -DLLDB_PYTHON_EXE_RELATIVE_PATH=${bindir} \
                  -DLLDB_PYTHON_EXT_SUFFIX=${SOLIBSDEV} \
"

DEPENDS = "binutils zlib zstd libffi libxml2 libxml2-native ninja-native swig-native"
DEPENDS:append:class-nativesdk = " clang17-crosssdk-${SDK_ARCH} virtual/nativesdk-cross-binutils nativesdk-python3"
DEPENDS:append:class-target = " clang17-cross-${TARGET_ARCH} gcc-cross-${TARGET_ARCH} python3 compiler-rt17 libcxx"

RRECOMMENDS:${PN} = "binutils"
RRECOMMENDS:${PN}:append:class-target = " libcxx-dev"

# patch out build host paths for reproducibility
do_compile:prepend:class-target() {
    sed -i -e "s,${STAGING_DIR_NATIVE},,g" \
        -e "s,${STAGING_DIR_TARGET},,g" \
        -e "s,${S},,g"  \
        -e "s,${B},,g" \
        ${B}/tools/llvm-config/BuildVariables.inc
}

do_install:append() {
    rm -rf ${D}${libdir}/python*/site-packages/six.py
}

do_install:append:class-target () {
    # Allow bin path to change based on YOCTO_ALTERNATE_EXE_PATH
    sed -i 's;${_IMPORT_PREFIX}/bin;${_IMPORT_PREFIX_BIN};g' ${D}${libdir}/cmake/llvm/LLVMExports-release.cmake

    # Insert function to populate Import Variables
    sed -i "4i\
if(DEFINED ENV{YOCTO_ALTERNATE_EXE_PATH})\n\
  execute_process(COMMAND \"llvm-config\" \"--bindir\" OUTPUT_VARIABLE _IMPORT_PREFIX_BIN OUTPUT_STRIP_TRAILING_WHITESPACE)\n\
else()\n\
  set(_IMPORT_PREFIX_BIN \"\${_IMPORT_PREFIX}/bin\")\n\
endif()\n" ${D}${libdir}/cmake/llvm/LLVMExports-release.cmake

    if [ -n "${LLVM_LIBDIR_SUFFIX}" ]; then
        mkdir -p ${D}${nonarch_libdir}
        mv ${D}${libdir}/clang ${D}${nonarch_libdir}/clang
        ln -rs ${D}${nonarch_libdir}/clang ${D}${libdir}/clang
        rmdir --ignore-fail-on-non-empty ${D}${libdir}
    fi
    for t in clang clang++ llvm-nm llvm-ar llvm-as llvm-ranlib llvm-strip llvm-objcopy llvm-objdump llvm-readelf \
        llvm-addr2line llvm-dwp llvm-size llvm-strings llvm-cov; do
        ln -sf $t ${D}${bindir}/${TARGET_PREFIX}$t
    done

    # reproducibility
    sed -i -e 's,${B},,g' ${D}${libdir}/cmake/llvm/LLVMConfig.cmake
}

do_install:append:class-native () {
    if ${@bb.utils.contains('PACKAGECONFIG', 'clangd', 'true', 'false', d)}; then
        install -Dm 0755 ${B}${BINPATHPREFIX}/bin/clangd-indexer ${D}${bindir}/clangd-indexer
    fi
    install -Dm 0755 ${B}${BINPATHPREFIX}/bin/clang-pseudo-gen ${D}${bindir}/clang-pseudo-gen
    install -Dm 0755 ${B}${BINPATHPREFIX}/bin/clang-tidy-confusable-chars-gen ${D}${bindir}/clang-tidy-confusable-chars-gen
    install -Dm 0755 ${B}${BINPATHPREFIX}/bin/clang-tblgen ${D}${bindir}/clang-tblgen
    install -Dm 0755 ${B}${BINPATHPREFIX}/bin/lldb-tblgen ${D}${bindir}/lldb-tblgen
    install -Dm 0755 ${B}${BINPATHPREFIX}/bin/llvm-min-tblgen ${D}${bindir}/llvm-min-tblgen
    for f in `find ${D}${bindir} -executable -type f -not -type l`; do
        test -n "`file -b $f|grep -i ELF`" && ${STRIP} $f
        echo "stripped $f"
    done
    ln -sf clang-tblgen ${D}${bindir}/clang-tblgen${PV}
    ln -sf llvm-tblgen ${D}${bindir}/llvm-tblgen${PV}
    ln -sf llvm-config ${D}${bindir}/llvm-config${PV}
}

do_install:append:class-nativesdk () {
    sed -i -e "s|${B}/./bin/||g" ${D}${libdir}/cmake/llvm/LLVMConfig.cmake
    if ${@bb.utils.contains('PACKAGECONFIG', 'clangd', 'true', 'false', d)}; then
        install -Dm 0755 ${B}${BINPATHPREFIX}/bin/clangd-indexer ${D}${bindir}/clangd-indexer
    fi
    install -Dm 0755 ${B}${BINPATHPREFIX}/bin/clang-tblgen ${D}${bindir}/clang-tblgen
    install -Dm 0755 ${B}${BINPATHPREFIX}/bin/clang-pseudo-gen ${D}${bindir}/clang-pseudo-gen
    install -Dm 0755 ${B}${BINPATHPREFIX}/bin/clang-tidy-confusable-chars-gen ${D}${bindir}/clang-tidy-confusable-chars-gen
    for f in `find ${D}${bindir} -executable -type f -not -type l`; do
        test -n "`file -b $f|grep -i ELF`" && ${STRIP} $f
    done
    ln -sf clang-tblgen ${D}${bindir}/clang-tblgen${PV}
    ln -sf llvm-tblgen ${D}${bindir}/llvm-tblgen${PV}
    ln -sf llvm-config ${D}${bindir}/llvm-config${PV}
    rm -rf ${D}${datadir}/llvm/cmake
    rm -rf ${D}${datadir}/llvm
}

PACKAGES =+ "${PN}-libllvm ${PN}-lldb-python ${PN}-libclang-cpp ${PN}-tidy ${PN}-format ${PN}-tools \
             libclang17 lldb17 lldb17-server liblldb17 llvm17-linker-tools"

PROVIDES += "llvm17 llvm17-${PV}"
PROVIDES:append:class-native = " llvm17-native"

BBCLASSEXTEND = "native nativesdk"

RDEPENDS:lldb17 += "${PN}-lldb-python lldb17-server"

RDEPENDS:${PN}-tools += "\
  perl-module-digest-md5 \
  perl-module-file-basename \
  perl-module-file-copy \
  perl-module-file-find \
  perl-module-file-path \
  perl-module-findbin \
  perl-module-hash-util \
  perl-module-sys-hostname \
  perl-module-term-ansicolor \
"

RRECOMMENDS:${PN}-tidy += "${PN}-tools"

FILES:llvm17-linker-tools = "${libdir}/LLVMgold* ${libdir}/libLTO.so.* ${libdir}/LLVMPolly*"

FILES:${PN}-libclang-cpp = "${libdir}/libclang-cpp.so.*"

FILES:${PN}-lldb-python = "${libdir}/python*/site-packages/lldb/*"

FILES:${PN}-tidy = "${bindir}/*clang-tidy*"
FILES:${PN}-format = "${bindir}/*clang-format*"

FILES:${PN}-tools = "${bindir}/analyze-build \
  ${bindir}/c-index-test \
  ${bindir}/clang-apply-replacements \
  ${bindir}/clang-change-namespace \
  ${bindir}/clang-check \
  ${bindir}/clang-doc \
  ${bindir}/clang-extdef-mapping \
  ${bindir}/clang-include-fixer \
  ${bindir}/clang-linker-wrapper \
  ${bindir}/clang-move \
  ${bindir}/clang-nvlink-wrapper \
  ${bindir}/clang-offload-bundler \
  ${bindir}/clang-offload-packager \
  ${bindir}/clang-pseudo \
  ${bindir}/clang-query \
  ${bindir}/clang-refactor \
  ${bindir}/clang-rename \
  ${bindir}/clang-reorder-fields \
  ${bindir}/clang-repl \
  ${bindir}/clang-scan-deps \
  ${bindir}/diagtool \
  ${bindir}/find-all-symbols \
  ${bindir}/hmaptool \
  ${bindir}/hwasan_symbolize \
  ${bindir}/intercept-build \
  ${bindir}/modularize \
  ${bindir}/pp-trace \
  ${bindir}/sancov \
  ${bindir}/scan-build \
  ${bindir}/scan-build-py \
  ${bindir}/scan-view \
  ${bindir}/split-file \
  ${libdir}/libscanbuild/* \
  ${libdir}/libear/* \
  ${libexecdir}/analyze-c++ \
  ${libexecdir}/analyze-cc \
  ${libexecdir}/c++-analyzer \
  ${libexecdir}/ccc-analyzer \
  ${libexecdir}/intercept-c++ \
  ${libexecdir}/intercept-cc \
  ${datadir}/scan-build/* \
  ${datadir}/scan-view/* \
  ${datadir}/opt-viewer/* \
  ${datadir}/clang/* \
"

FILES:${PN} += "\
  ${bindir}/clang-cl \
  ${libdir}/BugpointPasses.so \
  ${libdir}/LLVMHello.so \
  ${libdir}/*Plugin.so \
  ${libdir}/${BPN} \
  ${nonarch_libdir}/clang/*/include/ \
"

FILES:lldb17 = "\
  ${bindir}/lldb \
  ${bindir}/lldb-argdumper \
  ${bindir}/lldb-instr \
  ${bindir}/lldb-vscode \
"

FILES:lldb17-server = "\
  ${bindir}/lldb-server \
"

FILES:liblldb17 = "\
  ${libdir}/liblldbIntelFeatures.so.* \
  ${libdir}/liblldb.so.* \
"

FILES:${PN}-libllvm =+ "\
  ${libdir}/libLLVM-${MAJOR_VER}.${MINOR_VER}.so \
  ${libdir}/libLLVM-${MAJOR_VER}.so \
  ${libdir}/libLLVM-${MAJOR_VER}git.so \
  ${libdir}/libLLVM-${MAJOR_VER}.${MINOR_VER}git.so \
  ${libdir}/libRemarks.so.* \
"

FILES:libclang17 = "\
  ${libdir}/libclang.so.* \
"

FILES:${PN}-dev += "\
  ${datadir}/llvm/cmake \
  ${libdir}/cmake \
  ${nonarch_libdir}/libear \
  ${nonarch_libdir}/${BPN}/*.la \
"

FILES:${PN}-staticdev += "${nonarch_libdir}/${BPN}/*.a"

FILES:${PN}-staticdev:remove = "${libdir}/${BPN}/*.a"
FILES:${PN}-dev:remove = "${libdir}/${BPN}/*.la"
FILES:${PN}:remove = "${libdir}/${BPN}/*"


INSANE_SKIP:${PN} += "already-stripped"
#INSANE_SKIP:${PN}-dev += "dev-elf"
INSANE_SKIP:${PN}-lldb-python += "dev-so dev-deps"
INSANE_SKIP:${MLPREFIX}liblldb = "dev-so"

#Avoid SSTATE_SCAN_COMMAND running sed over llvm-config.
SSTATE_SCAN_FILES:remove = "*-config"

TOOLCHAIN = "clang17"
TOOLCHAIN:class-native = "gcc"
TOOLCHAIN:class-nativesdk = "clang17"
COMPILER_RT:class-nativesdk:toolchain-clang:runtime-llvm = "-rtlib=libgcc --unwindlib=libgcc"
LIBCPLUSPLUS:class-nativesdk:toolchain-clang:runtime-llvm = "-stdlib=libstdc++"

SYSROOT_DIRS:append:class-target = " ${nonarch_libdir}"

SYSROOT_PREPROCESS_FUNCS:append:class-target = " clang_sysroot_preprocess"

clang_sysroot_preprocess() {
	install -d ${SYSROOT_DESTDIR}${bindir_crossscripts}/
	install -m 0755 ${S}/../sources-unpack/llvm-config ${SYSROOT_DESTDIR}${bindir_crossscripts}/
	ln -sf llvm-config ${SYSROOT_DESTDIR}${bindir_crossscripts}/llvm-config${PV}
	# LLDTargets.cmake references the lld executable(!) that some modules/plugins link to
	install -d ${SYSROOT_DESTDIR}${bindir}

	binaries="lld diagtool clang-${MAJOR_VER} clang-format clang-offload-packager
	                clang-offload-bundler clang-scan-deps clang-repl
	                clang-rename clang-refactor clang-check clang-extdef-mapping clang-apply-replacements
	                clang-reorder-fields clang-tidy clang-change-namespace clang-doc clang-include-fixer
	                find-all-symbols clang-move clang-query pp-trace clang-pseudo modularize"

	if ${@bb.utils.contains('PACKAGECONFIG', 'clangd', 'true', 'false', d)}; then
	        binaries="${binaries} clangd"
	fi

	for f in ${binaries}
	do
		install -m 755 ${D}${bindir}/$f ${SYSROOT_DESTDIR}${bindir}/
	done
}
