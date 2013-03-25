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
 * Question type class for the funCKit question type.
 *
 * @package    qtype
 * @subpackage funckit
 * @copyright  2012 onwards Lukas Elsner  {@link http://www.mindrunner.de}
 * @license    http://www.gnu.org/copyleft/gpl.html GNU GPL v3 or later
 */


defined('MOODLE_INTERNAL') || die();
require_once($CFG->dirroot.'/question/type/funckit/toolbox.php');

/**
 * The true-false question type class.
 *
 * @copyright  2012 onwards Lukas Elsner  {@link http://www.mindrunner.de}
 * @license    http://www.gnu.org/copyleft/gpl.html GNU GPL v3 or later
 */
class qtype_funckit extends question_type {

    public function extra_question_fields() {
        return array('question_funckit', 'fckbegin', 'fcksolution', 'simassign', 'cmp0', 'cmp1', 'cmp2', 'cmp3', 'correctfeedback', 'incorrectfeedback');
    }

    private function fileoptions() {
        $fileroptions = array();
        $fileroptions['maxbytes'] = 0;
        $fileroptions['maxfiles'] = 1;
        $fileroptions['subdirs'] = 0;
        return $fileroptions;
    }

    public function save_question_options($question) {
        global $DB;
        $fs = get_file_storage();
        $new = false;

        if (!($options = $DB->get_record('question_funckit', array('questionid' => $question->id)))) {
            $options = new stdClass();
            $new = true;
        }
        $options->questionid    = $question->id;
        $options->correctfeedback    = $question->correctfeedback['text'];
        $options->incorrectfeedback    = $question->incorrectfeedback['text'];

        foreach (FuckitLib::get_funckit_elements() as $element) {
            if(FuckitLib::file_uploaded($question->$element['name'])) {
                $options->$element['name'] = $question->$element['name'];
                file_save_draft_area_files($question->$element['name'], $question->context->id, 'qtype_'.$this->name(), $element['name'], $question->id, $this->fileoptions());
                //TODO: Is this correct? Always contextid==5 ???
                $fs->delete_area_files(5, "user", "draft", $question->$element['name']);
            } else {
                $options->$element['name'] = null;

                if (isset($element['mandatory']) && $element['mandatory'] === true) {
                    $result = new stdClass();
                    $result->error = "No data for field '". $element['name'] ."' when saving " . $this->name() . " question id " . $question->id . " (field is mandatory).";
                    return $result;
                }
            }
        }

        if($new) {
            $DB->insert_record('question_funckit', $options);
        } else {
            $DB->update_record('question_funckit', $options);
        }
        return true;
    }


    function get_question_options($question) {
        global $DB, $OUTPUT;
        if (!$question->options = $DB->get_record('question_funckit', array('questionid' => $question->id))) {
            echo $OUTPUT->notification('Error: Missing question options!');
            return false;
        }
        return true;
    }

    public function delete_question($questionid, $contextid) {
        parent::delete_question($questionid, $contextid);
        global $DB;
        $DB->delete_records('question_funckit', array('questionid' => $questionid));
    }

    public function move_files($questionid, $oldcontextid, $newcontextid) {
        parent::move_files($questionid, $oldcontextid, $newcontextid);
        $fs = get_file_storage();
        $files = array('fckbegin', 'fcksolution', 'simassign', 'cmp0', 'cmp1', 'cmp2', 'cmp3');
        foreach($files as $file) {
            $fs->move_area_files_to_new_context($oldcontextid, $newcontextid, 'qtype_'.$this->name(), $file, $questionid);
        }
    }

    protected function delete_files($questionid, $contextid) {
        parent::delete_files($questionid, $contextid);
        $fs = get_file_storage();
        $files = array('fckbegin', 'fcksolution', 'simassign', 'cmp0', 'cmp1', 'cmp2', 'cmp3');
        foreach($files as $file) {
            $fs->delete_area_files($contextid, 'qtype_'.$this->name(), $file, $questionid);
        }
    }

    public function get_random_guess_score($questiondata) {
        return null;
    }

    public function get_possible_responses($questiondata) {
        return array(
            $questiondata->id => array(
                0 => new question_possible_response(false, "false"),
                1 => new question_possible_response(true, "true"),
                null => question_possible_response::no_response()
            )
        );
    }

    public function export_to_xml($question, qformat_xml $format, $extra = null) {
        //TODO: implement export method
        return '';
    }

    public function import_from_xml($data, $question, qformat_xml $format, $extra=null) {
        //TODO: implement import method
        return $question;
    }
}
