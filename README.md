Wikipedia Corpus Converter
=========================

This project was developed at Institut für Deutsche Sprache (IDS) in 
Mannheim, Germany. The aim of the project was to automatically generate 
Wikipedia corpora in I5 format (http://corpora.ids-mannheim.de/I5/DTD/i5.dtd),  
the XML format used in the German Reference Corpora (Deutsches Referenzkorpus - DeReKo) hosted by IDS.

This task is done in two stages: firstly wikitext is converted to XML, and 
secondly the XML-ized Wikipedia pages are converted to I5. For the first conversion stage, 
we use Sweble parser version 2.0.0-alpha-2-SNAPSHOT, and for the second conversion stage, Saxon-EE 9.4.0.3J. A Saxon-HE version is not applicable, because the XSLT Stylesheets 
converting XML to I5 make use of Saxon extension functions.


For questions and suggestions, please contact Eliza Margaretha 
(margaretha at ids-mannheim.de) or Harald Lüngen (luengen at ids-mannheim.de).

The codes in this project is licensed under GPL v3.
