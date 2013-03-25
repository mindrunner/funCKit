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
 * funCKit question definition class.
 *
 * @package    qtype
 * @subpackage funckit
 * @copyright  2012 onwards Lukas Elsner  {@link http://www.mindrunner.de}
 * @license    http://www.gnu.org/copyleft/gpl.html GNU GPL v3 or later
 */


defined('MOODLE_INTERNAL') || die();


/**
 * Represents a funckit question.
 *
 * @copyright  2012 onwards Lukas Elsner  {@link http://www.mindrunner.de}
 * @license    http://www.gnu.org/copyleft/gpl.html GNU GPL v3 or later
 */
class qtype_funckit_question extends question_graded_automatically {
    public $rightanswer = true;


    public function get_expected_data() {
        return array('answer' => PARAM_BOOL);
    }

    public function get_correct_response() {
        return array('answer' => $this->rightanswer);
    }

    public function summarise_response(array $response) {
        if (!array_key_exists('answer', $response)) {
            return null;
        } else if ($response['answer']) {
            return "true";
        } else {
            return "false";
        }
    }

    public function classify_response(array $response) {
        if (!array_key_exists('answer', $response)) {
            return array($this->id => question_classified_response::no_response());
        }
        list($fraction) = $this->grade_response($response);
        if ($response['answer']) {
            return array($this->id => new question_classified_response(1, true, $fraction));
        } else {
            return array($this->id => new question_classified_response(0, false, $fraction));
        }
    }

    public function is_complete_response(array $response) {
        return array_key_exists('answer', $response);
    }

    public function get_validation_error(array $response) {
        //Since funCKit applet is responsible for responses, we can just return an empty string here!
        return '';
    }

    public function is_same_response(array $prevresponse, array $newresponse) {
        return question_utils::arrays_same_at_key_missing_is_blank($prevresponse, $newresponse, 'answer');
    }

    public function grade_response(array $response) {
        if ($this->rightanswer == $response['answer']) {
            $fraction = 1;
        } else {
            $fraction = 0;
        }
        return array($fraction, question_state::graded_state_for_fraction($fraction));
    }

    public function check_file_access($qa, $options, $component, $filearea, $args, $forcedownload) {
        $isfunckitfile = $component == "qtype_funckit"
            && in_array($filearea, FuckitLib::get_funckit_element_names());
        $isembeddedtextfile = $component == "question"
            && ($filearea == "questiontext"
                || $filearea == "correctanswer"
                || $filearea == "incorrectanswer"
            );
        return $isfunckitfile || $isembeddedtextfile;
    }
}
