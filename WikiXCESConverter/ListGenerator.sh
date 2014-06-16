# Eliza Margaretha, Mar 2013
# Institut für Deutsche Sprache

function createList {
    echo "<"$1">" > $2
    
    for index in $(find $3/$1/ -type d | sed 's/.*\///' | sort);
    do  
        echo "  <index value=\""$index"\">" >> $2 
        find $3/$1/$index/ -type f 2>/dev/null | sed 's/.*\/\(.*\)\.xml/\1/' | sort -n | sed 's/\(.*\)/    <id>\1<\/id>/' >> $2        
        echo "  </index>" >> $2
    done

    echo "</"$1">" >> $2    
}


# variables

type=$1
xmlFolder=$2
output=$3

echo "Listing articles by index ..."    
createList $type $output $xmlFolder