all: 
	for i in *.xml ;  do \
	echo $$i ; \
	xmllint --noout $$i ; \
	done;
	cvs commit -m ""
