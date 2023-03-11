# Recipe created by recipetool
# This is the basis of a recipe and may need further editing in order to be fully functional.
# (Feel free to remove these comments when editing.)

# WARNING: the following LICENSE and LIC_FILES_CHKSUM values are best guesses - it is
# your responsibility to verify that the values are complete and correct.
#
# The following license files were not able to be identified and are
# represented as "Unknown" below, you will need to check them yourself:
#   LICENSE
#LICENSE = "Unknown"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"
#SUMMARY = "Example of how to build an external Linux kernel module"
#DESCRIPTION = "${SUMMARY}"
#LICENSE = "GPL-2.0-only"
#LIC_FILES_CHKSUM = "file://COPYING;md5=12f884d2ae1ff87c09e5b7ccc2c4ca7e"

inherit module

SRC_URI = "git://git@github.com/cu-ecen-aeld/assignment-7-VamboozerCU.git;protocol=ssh;branch=main \
           git://git@github.com/cu-ecen-aeld/assignments-3-and-later-VamboozerCU.git;protocol=ssh;branch=main \
           file://misc-modules-init.sh \
           file://aesdchar-init.sh \
          "
# file://misc-modules.rules

# Modify these as desired
PV = "1.0+git${SRCPV}"
SRCREV = "4126ce4bb910b46d442322db49722f225c7c9c59"

S = "${WORKDIR}/git/misc-modules"

EXTRA_OEMAKE:append:task-install = " -C ${STAGING_KERNEL_DIR} M=${S}"
EXTRA_OEMAKE += "KERNELDIR=${STAGING_KERNEL_DIR}"

FILES:${PN} += "${sysconfdir}/init.d/misc-modules-init"
FILES:${PN} += "${sysconfdir}/init.d/aesdchar-init"
#/etc/udev 
#/etc/udev/rules.d 
#/etc/udev/rules.d/misc-modules.rules 

inherit update-rc.d
INITSCRIPT_PACKAGES = "${PN}"
INITSCRIPT_NAME:${PN} = "misc-modules-init"
INITSCRIPT_NAME:${PN} += "aesdchar-init"

RPROVIDES:${PN} += "kernel-module-misc-modules"
#RPROVIDES:${PN} += "kernel-module-hello"
#RPROVIDES:${PN} += "kernel-module-faulty"

#KERNEL_MODULE_AUTOLOAD += "hello"
#KERNEL_MODULE_AUTOLOAD += "faulty"

FILES:${PN} += "${bindir}/module_load \
                ${bindir}/module_unload \
                ${bindir}/aesdchar_load \
                ${bindir}/aesdchar_unload \
                ${sysconfdir}/init.d \
               "

# MODULE_EXTRA_DEPENDENCIES += "/lib/modules/${KERNEL_VERSION}/..."

do_compile () {
	oe_runmake
}

do_install () {
	# TODO: Install your binaries/scripts here.
	# Be sure to install the target directory with install -d first
	# Yocto variables ${D} and ${S} are useful here, which you can read about at 
	# https://docs.yoctoproject.org/ref-manual/variables.html?highlight=workdir#term-D
	# and
	# https://docs.yoctoproject.org/ref-manual/variables.html?highlight=workdir#term-S
	# See example at https://github.com/cu-ecen-aeld/ecen5013-yocto/blob/ecen5013-hello-world/meta-ecen5013/recipes-ecen5013/ecen5013-hello-world/ecen5013-hello-world_git.bb
	install -d ${D}${sysconfdir}/init.d
    install -m 0755 ${WORKDIR}/misc-modules-init.sh ${D}${sysconfdir}/init.d/misc-modules-init
    install -m 0755 ${WORKDIR}/aesdchar-init.sh ${D}${sysconfdir}/init.d/aesdchar-init
    
    install -d ${D}${bindir}
    install -m 0755 ${S}/module_load ${D}${bindir}/module_load
	install -m 0755 ${S}/module_unload ${D}${bindir}/module_unload
    install -m 0755 ${S}/aesdchar_load ${D}${bindir}/aesdchar_load
	install -m 0755 ${S}/aesdchar_unload ${D}${bindir}/aesdchar_unload

    install -d ${D}/lib/modules/${KERNEL_VERSION}/kernel/drivers/hello
    install -m 0644 ${S}/hello.ko ${D}/lib/modules/${KERNEL_VERSION}/kernel/drivers/hello
    install -d ${D}/lib/modules/${KERNEL_VERSION}/kernel/drivers/faulty
    install -m 0644 ${S}/faulty.ko ${D}/lib/modules/${KERNEL_VERSION}/kernel/drivers/faulty

    #install -d ${D}/etc/udev/rules.d
    #install -m 0644 ${WORKDIR}/misc-modules.rules ${D}/etc/udev/rules.d
}
