<?php
class Point2D {
    var $x, $y;
    var $label;

    function Point2D($x, $y) {
        $this->x = $x;
        $this->y = $y;
    }

    function setLabel($label) {
        $this->label = $label;
    }


}

// "$label" is declared but not defined
$p1 = new Point2D(1.233, 3.445);
//print_r(get_object_vars($p1));

echo "<br \>";
echo  json_encode($p1);



?> 