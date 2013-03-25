CC := texi2pdf
#--batch==no interaction
CFLAGS := --batch 
NAME := handbuch
OUTNAME := /var/lib/redmine/public/tex/$(NAME).pdf

default:
	./vc
	-rm -f $(NAME).glo 
	-rm -f $(NAME).gls
	-rm -f $(NAME).glg
	-rm -f $(NAME).ist
	$(CC) $(CFLAGS) $(NAME).tex
	makeglossaries $(NAME).glo
	$(CC) $(CFLAGS) $(NAME).tex
	rm -f vc.tex

buildhost:
	./vc
	$(CC) $(CFLAGS) $(NAME).tex
	makeglossaries $(NAME).glo
	$(CC) $(CFLAGS) -o $(OUTNAME) $(NAME).tex
	cp $(NAME).pdf $(OUTNAME)
	rm -f vc.tex

## build without git info
release:
	-rm -f vc.tex
	$(CC) $(CFLAGS) $(NAME).tex
	makeglossaries $(NAME).glo
	$(CC) $(CFLAGS) $(NAME).tex

