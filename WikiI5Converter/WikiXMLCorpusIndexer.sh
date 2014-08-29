# Indexing WikiXML pages 
#
# Eliza Margaretha, Aug 2014
# Institut für Deutsche Sprache
# This script is licensed under GPL v3.

function createList {
    echo "<"$1">" > $2
    
    for index in $(find $3 -type d | sed 's/.*\///' | sort);
    do  
        echo "  <index value=\""$index"\">" >> $2 
        find $3/$index/ -type f 2>/dev/null | sed 's/.*\/\(.*\)\.xml/\1/' | sort -n | sed 's/\(.*\)/    <id>\1<\/id>/' >> $2        
        echo "  </index>" >> $2
    done

    echo "</"$1">" >> $2    
}


# variables

pageType=$1
xmlFolder=$2
output=$3

if [ -z "$1" ];
then
    echo "Please specify the Wikipage type [articles|discussions]:  "
    read pageType
fi

if [ -z "$2" ];
then
    echo "Please specify the WikiXML article or discussion folder: "
    read xmlFolder
fi

if [ -z "$3" ];
then
    echo "Please specify the output file: "
    read output
fi



echo "Indexing WikiXML pages"    
createList $pageType $output $xmlFolder
