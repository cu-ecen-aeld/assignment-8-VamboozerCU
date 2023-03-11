# Recipe created by recipetool
# This is the basis of a recipe and may need further editing in order to be fully functional.
# (Feel free to remove these comments when editing.)

# Unable to find any files that looked like license statements. Check the accompanying
# documentation and source headers and set LICENSE and LIC_FILES_CHKSUM accordingly.
#
# NOTE: LICENSE is being set to "CLOSED" to allow you to at least start building - if
# this is not accurate with respect to the licensing of the software being built (it
# will not be in most cases) you must specify the correct value before using this
# recipe for anything other than initial testing/development!
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

inherit module

# No information for SRC_URI yet (only an external source tree was specified)
SRC_URI = "git://git@github.com/cu-ecen-aeld/assignments-3-and-later-VamboozerCU.git;protocol=ssh;branch=main \
		   file://aesdchar-init.sh \
		   "
#SRC_URI += "file://files/example.txt"

PV = "1.0+git${SRCPV}"
SRCREV = "a1b9ac400edeba6d67e7ad8f329936feb4f5897a"

S = "${WORKDIR}/git/aesd-char-driver"

EXTRA_OEMAKE:append:task-install = " -C ${STAGING_KERNEL_DIR} M=${S}"
EXTRA_OEMAKE += "KERNELDIR=${STAGING_KERNEL_DIR}"

FILES:${PN} += "${sysconfdir}/init.d/aesdchar-init"

inherit update-rc.d
INITSCRIPT_PACKAGES = "${PN}"
INITSCRIPT_NAME:${PN} = "aesdchar-init"

RPROVIDES:${PN} += "kernel-module-aesdchar"
#KERNEL_MODULE_AUTOLOAD += "aesdchar"

FILES:${PN} += "${bindir}/aesdchar_load \
                ${bindir}/aesdchar_unload \
                ${sysconfdir}/init.d \
               "

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
	install -m 0755 ${WORKDIR}/aesdchar-init.sh ${D}${sysconfdir}/init.d/aesdchar-init
    
    install -d ${D}${bindir}
    install -m 0755 ${S}/aesdchar_load ${D}${bindir}/aesdchar_load
	install -m 0755 ${S}/aesdchar_unload ${D}${bindir}/aesdchar_unload

    install -d ${D}/lib/modules/${KERNEL_VERSION}/kernel/drivers/aesdchar
    install -m 0644 ${S}/aesdchar.ko ${D}/lib/modules/${KERNEL_VERSION}/kernel/drivers/aesdchar
}