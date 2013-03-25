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
 * Defines the editing form for the funCKit question type.
 *
 * @package    qtype
 * @subpackage funckit
 * @copyright  2012 onwards Lukas Elsner  {@link http://www.mindrunner.de}
 * @license    http://www.gnu.org/copyleft/gpl.html GNU GPL v3 or later
 */


defined('MOODLE_INTERNAL') || die();

require_once($CFG->dirroot.'/question/type/edit_question_form.php');
require_once($CFG->dirroot.'/question/type/funckit/toolbox.php');


/**
 * True-false question editing form definition.
 *
 * @copyright  2012 onwards Lukas Elsner  {@link http://www.mindrunner.de}
 * @license    http://www.gnu.org/copyleft/gpl.html GNU GPL v3 or later
 */
class qtype_funckit_edit_form extends question_edit_form {


    /**
     *
     * Options shared by all file pickers in the form.
     */
    private function file_picker_options($ext) {
        $filepickeroptions = array();
        $filepickeroptions['accepted_types'] = array('*.'.$ext);
        $filepickeroptions['maxbytes'] = 0;
        $filepickeroptions['maxfiles'] = 1;
        $filepickeroptions['subdirs'] = 0;
        return $filepickeroptions;
    }

    protected function definition_inner($mform) {
        foreach (FuckitLib::get_funckit_elements() as $element) {
            $mform->addElement('filepicker', $element['name'], get_string($element['name'], 'qtype_'.$this->qtype()), null, $this->file_picker_options($element['filetype']));
        }

        $mform->addElement('editor', 'correctfeedback', get_string('correctfeedback', 'qtype_'.$this->qtype()), array('rows' => 10), $this->editoroptions);
        $mform->setType('correctfeedback', PARAM_RAW);
        $mform->addElement('editor', 'incorrectfeedback', get_string('incorrectfeedback', 'qtype_'.$this->qtype()), array('rows' => 10), $this->editoroptions);
        $mform->setType('incorrectfeedback', PARAM_RAW);
        $mform->addRule('fcksolution', get_string('formerror_nofcksolution', 'qtype_'.$this->qtype()), 'required', null, 'client');
        $mform->addRule('correctfeedback', null, 'required', null, 'client');
        $mform->addRule('incorrectfeedback', null, 'required', null, 'client');
    }

    public function set_data($question) {
        $draftid = file_get_submitted_draft_itemid('correctfeedback');


        if(empty($question->options)) $question->options = new stdClass();

        if (!empty($question->options->correctfeedback)) {
            $correctfeedback = $question->options->correctfeedback;
        } else {
            $correctfeedback = '';
        }
        $correctfeedback = file_prepare_draft_area($draftid, $this->context->id,
            'question', 'correctfeedback', empty($question->id) ? null : (int) $question->id,
            $this->fileoptions, $correctfeedback);

        $question->options->correctfeedback = array();
        $question->options->correctfeedback['text'] = $correctfeedback;
        $question->options->correctfeedback['format'] = empty($question->correctfeedbackformat) ?
            editors_get_preferred_format() : $question->correctfeedbackformat;
        $question->options->correctfeedback['itemid'] = $draftid;

        
        $draftid = file_get_submitted_draft_itemid('incorrectfeedback');

        if (!empty($question->options->incorrectfeedback)) {
            $incorrectfeedback = $question->options->incorrectfeedback;
        } else {
            $incorrectfeedback = '';
        }
        $incorrectfeedback = file_prepare_draft_area($draftid, $this->context->id,
            'question', 'incorrectfeedback', empty($question->id) ? null : (int) $question->id,
            $this->fileoptions, $incorrectfeedback);

        $question->options->incorrectfeedback = array();
        $question->options->incorrectfeedback['text'] = $incorrectfeedback;
        $question->options->incorrectfeedback['format'] = empty($question->incorrectfeedbackformat) ?
            editors_get_preferred_format() : $question->incorrectfeedbackformat;
        $question->options->incorrectfeedback['itemid'] = $draftid;

        parent::set_data($question);
    }

    public function data_preprocessing($question) {
        $question = parent::data_preprocessing($question);

        foreach (FuckitLib::get_funckit_elements() as $element) {
            $draftitemid = file_get_submitted_draft_itemid($element['name']);
            file_prepare_draft_area($draftitemid, $this->context->id, 'qtype_'.$this->qtype(), $element['name'], !empty($question->id) ? (int) $question->id : null, $this->file_picker_options($element['filetype']));
            $question->$element['name'] = $draftitemid;
        }

        return $question;
    }

    public function validation($data, $files) {
        $errors = parent::validation($data, $files);
        if (!FuckitLib::file_uploaded($data['fcksolution'])) {
            $errors["fcksolution"] = get_string('formerror_nofcksolution', 'qtype_'.$this->qtype());
        }
        return $errors;
    }

    public function qtype() {
        return 'funckit';
    }
}
