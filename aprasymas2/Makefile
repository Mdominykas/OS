generuoti:
	xelatex projektas2.tex
	biber projektas2
	xelatex projektas2.tex

ubuntu:
	echo "Diegiamas LaTeX (PdfLaTeX, XeTeX ir kt.)"
	sudo apt-get install texlive-full
	echo "Diegiama literatūros sąrašo tvarkyklė Biber skirta BibLaTeX paketui"
	sudo apt-get install biber
	echo "Diegiami OpenType šriftai"
	sudo apt-get install fonts-texgyre
	echo "Diegiamas Palemonas šriftas į sistemą"
	sudo cp -r Palemonas-2.1 /usr/share/fonts/truetype/

clean:
	git clean -dfx
