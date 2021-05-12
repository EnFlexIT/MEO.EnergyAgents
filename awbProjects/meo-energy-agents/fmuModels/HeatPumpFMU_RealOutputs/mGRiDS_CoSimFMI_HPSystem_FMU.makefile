# FIXME: before you push into master...
RUNTIMEDIR=C:/OpenModelica1.12.0-64bit//include/omc/c/
OMC_MINIMAL_RUNTIME=1
OMC_FMI_RUNTIME=1
include $(RUNTIMEDIR)/Makefile.objs
#COPY_RUNTIMEFILES=$(FMI_ME_OBJS:%= && (OMCFILE=% && cp $(RUNTIMEDIR)/$$OMCFILE.c $$OMCFILE.c))

fmu:
	rm -f mGRiDS_CoSimFMI_HPSystem.fmutmp/sources/mGRiDS_CoSimFMI_HPSystem_init.xml
	cp -a C:/OpenModelica1.12.0-64bit//include/omc/c/* mGRiDS_CoSimFMI_HPSystem.fmutmp/sources/include/
	cp -a C:/OpenModelica1.12.0-64bit//share/omc/runtime/c/fmi/buildproject/* mGRiDS_CoSimFMI_HPSystem.fmutmp/sources
	cp -a mGRiDS_CoSimFMI_HPSystem_FMU.libs mGRiDS_CoSimFMI_HPSystem.fmutmp/sources/

