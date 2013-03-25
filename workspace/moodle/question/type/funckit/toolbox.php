<?php
/**
 * Toolbox
 *
 * @since      2.0
 * @package    qtype
 * @subpackage funckit
 * @copyright  2012 onwards Lukas Elsner  {@link http://www.mindrunner.de}
 * @license    http://www.gnu.org/copyleft/gpl.html GNU GPL v3 or later
 */


class FuckitLib {
    // TODO can we generate this out of somewhere? like install.xml
    private static $elements = array(
        array(
            'name' => 'fckbegin',
            'filetype' => 'fck'
        ),
        array(
            'name' => 'fcksolution',
            'filetype' => 'fck',
            'mandatory' => true
        ),
        array(
            'name' => 'simassign',
            'filetype' => 'txt'
        ),
        array(
            'name' => 'cmp0',
            'filetype' => 'cmp'
        ),
        array(
            'name' => 'cmp1',
            'filetype' => 'cmp'
        ),
        array(
            'name' => 'cmp2',
            'filetype' => 'cmp'
        ),
        array(
            'name' => 'cmp3',
            'filetype' => 'cmp'
        )
    );

    public static function file_uploaded($draftitemid) {
        $draftareafiles = file_get_drafarea_files($draftitemid);
        do {
            $draftareafile = array_shift($draftareafiles->list);
        } while ($draftareafile !== null && $draftareafile->filename == '.');
        if ($draftareafile === null) {
            return false;
        }
        return true;
    }

    public static function get_funckit_elements() {
        return self::$elements;
    }

    public static function get_funckit_element_names() {
        return array_map(function (array $element) { return $element['name']; }, self::$elements);
    }
}
