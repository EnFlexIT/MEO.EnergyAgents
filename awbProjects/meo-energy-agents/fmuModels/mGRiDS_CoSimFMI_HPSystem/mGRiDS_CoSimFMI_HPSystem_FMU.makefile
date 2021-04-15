# FIXME: before you push into master...
RUNTIMEDIR=C:/Program Files/OpenModelica1.17.0-64bit/include/omc/c/
#COPY_RUNTIMEFILES=$(FMI_ME_OBJS:%= && (OMCFILE=% && cp $(RUNTIMEDIR)/$$OMCFILE.c $$OMCFILE.c))

fmu:
	rm -f mGRiDS_CoSimFMI_HPSystem.fmutmp/sources/mGRiDS_CoSimFMI_HPSystem_init.xml
	cp -a "C:/Program Files/OpenModelica1.17.0-64bit/share/omc/runtime/c/fmi/buildproject/"* mGRiDS_CoSimFMI_HPSystem.fmutmp/sources
	cp -a mGRiDS_CoSimFMI_HPSystem_FMU.libs mGRiDS_CoSimFMI_HPSystem.fmutmp/sources/

