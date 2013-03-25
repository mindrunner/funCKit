<?php
// This file is part of Moodle - http://moodle.org/
//
// Moodle is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// Moodle is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with Moodle.  If not, see <http://www.gnu.org/licenses/>.

/**
 * funCKit question renderer class
 *
 * @package    qtype
 * @subpackage funckit
 * @copyright  2012 onwards Lukas Elsner  {@link http://www.mindrunner.de}
 * @license    http://www.gnu.org/copyleft/gpl.html GNU GPL v3 or later
 */


defined('MOODLE_INTERNAL') || die();


/**
 * Generates the output for funckit questions.
 *
 * @copyright  2012 onwards Lukas Elsner  {@link http://www.mindrunner.de}
 * @license    http://www.gnu.org/copyleft/gpl.html GNU GPL v3 or later
 */
class qtype_funckit_renderer extends qtype_renderer {

    private function get_url_for_file(question_attempt $qa, $filearea, $itemid = 0) {
        $question = $qa->get_question();
        $qubaid = $qa->get_usage_id();
        $slot = $qa->get_slot();
        $fs = get_file_storage();
        $contextid = $question->contextid;
        $componentname = $question->qtype->plugin_name();
        $draftfiles = $fs->get_area_files($contextid, $componentname, $filearea, $itemid, 'id');
        if ($draftfiles) {
            foreach ($draftfiles as $file) {
                if ($file->is_directory()) {
                    continue;
                }
                $url = moodle_url::make_pluginfile_url($question->contextid, $componentname, $filearea, "$qubaid/$slot/{$itemid}", '/', $file->get_filename());
                return $url->out();
            }
        }
        return "";
    }

    public function get_field_prefix() {
        return '';
    }

    public function formulation_and_controls(question_attempt $qa, question_display_options $options) {
        global $CFG;
        $question = $qa->get_question();
        $result = "";
        $result .= html_writer::tag('div', $question->format_questiontext($qa), array('class' => 'qtext'));
        $width = '100%';
        $height = '800px';
        $fcksolution = $this->get_url_for_file($qa, 'fcksolution', $question->id);
        $fckbegin = $this->get_url_for_file($qa, 'fckbegin', $question->id);
        $simassign = $this->get_url_for_file($qa, 'simassign', $question->id);
        $sequencenr = $qa->get_slot();
        $cmp0 = $this->get_url_for_file($qa, 'cmp0', $question->id);
        $cmp1 = $this->get_url_for_file($qa, 'cmp1', $question->id);
        $cmp2 = $this->get_url_for_file($qa, 'cmp2', $question->id);
        $cmp3 = $this->get_url_for_file($qa, 'cmp3', $question->id);
        $archive = "$CFG->httpswwwroot/funckit.jar";
        if($options->feedback == 1) {
            $result .= '<applet code = \'de.sep2011.funckit.CircuitPresenterApplet\''
                .' archive = \''.$archive.'\''
                .' width = \''.$width.'\''
                .' height = \''.$height.'\''
                .' id = \'funCKitApplet\''
                .' MAYSCRIPT>'
                .'<param name="circuit" value="'.$fcksolution.'" />'
                .'<param name="session_id" value="'.session_id().'">'
                .'<param name="session_name" value="'.session_name().'">'
                .'</applet>';
        } else {
            $fieldname = "q".$qa->get_usage_id().":".$sequencenr."_answer";
            
	    $result .= '<script type="text/javascript"> 
	     		 window.onload = function(){
			  document.getElementById(\'responseform\').next.style.display = \'none\'
			 }
			</script>';
	    
	    $result .= '<applet code = \'de.sep2011.funckit.ELearningSolveApplet\''
                .' archive = \''.$archive.'\''
                .' width = \''.$width.'\''
                .' height = \''.$height.'\''
                .' id = \'funCKitApplet\''
                .' MAYSCRIPT>'
                .'<param name="fcksolution" value="'.$fcksolution.'" />'
                .'<param name="session_id" value="'.session_id().'">'
                .'<param name="session_name" value="'.session_name().'">'
                .'<param name="fckbegin" value="'.$fckbegin.'" />'
                .'<param name="simassign" value="'.$simassign.'" />'
                .'<param name="cmp0" value="'.$cmp0.'">'
                .'<param name="cmp1" value="'.$cmp1.'">'
                .'<param name="cmp2" value="'.$cmp2.'" />'
                .'<param name="cmp3" value="'.$cmp3.'">'
                .'<param name="fieldname" value="'.$fieldname.'">'
                .'</applet>';
            $result .= "<input type=\"hidden\" id=\"${fieldname}\" name=\"${fieldname}\" value=\"false\">";
        }
        return $result;
    }

    public function specific_feedback(question_attempt $qa) {
        $response = $qa->get_last_qt_var('answer', '');
        if ($response) {
            return $qa->get_question()->correctfeedback;
        } else {
            return $qa->get_question()->incorrectfeedback;
        }
    }
}
