package dog.pawbook.logic.commands;

import static dog.pawbook.logic.commands.CommandTestUtil.assertCommandSuccess;
import static dog.pawbook.testutil.Assert.assertThrows;
import static dog.pawbook.testutil.TypicalEntities.getDatabaseWithPrograms;
import static dog.pawbook.testutil.TypicalId.ID_FIFTEEN;
import static dog.pawbook.testutil.TypicalId.ID_FOUR;
import static dog.pawbook.testutil.TypicalId.ID_SIX;
import static dog.pawbook.testutil.TypicalId.ID_SIXTEEN;
import static dog.pawbook.testutil.TypicalId.ID_TWO;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import dog.pawbook.model.Model;
import dog.pawbook.model.ModelManager;
import dog.pawbook.model.UserPrefs;
import dog.pawbook.model.managedentity.IdMatchPredicate;
import dog.pawbook.model.managedentity.program.Program;

public class EnrolCommandTest {
    private Model model;
    private Model expectedModel;

    @BeforeEach
    public void setUp() {
        model = new ModelManager(getDatabaseWithPrograms(), new UserPrefs());
        expectedModel = new ModelManager(model.getDatabase(), new UserPrefs());
    }

    @Test
    public void constructor_nullEnrol_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new EnrolCommand(null, null));
    }

    @Test
    public void execute_enrolOneDogOneProgram_success() {
        Set<Integer> dogIdSet = new HashSet<>();
        Set<Integer> programIdSet = new HashSet<>();
        dogIdSet.add(ID_TWO);
        programIdSet.add(ID_FIFTEEN);

        Program editedProgram = (Program) expectedModel.getEntity(ID_FIFTEEN);

        HashSet<Integer> updatedEnrolledDogs = new HashSet<>(editedProgram.getDogIdSet());
        updatedEnrolledDogs.add(ID_TWO);

        expectedModel.setEntity(ID_FIFTEEN, new Program(editedProgram.getName(),
                editedProgram.getSessions(), editedProgram.getTags(), updatedEnrolledDogs));

        expectedModel.updateFilteredEntityList(new IdMatchPredicate(programIdSet));

        EnrolCommand enrolCommand = new EnrolCommand(dogIdSet, programIdSet);
        String expectedMessage = String.format(EnrolCommand.MESSAGE_SUCCESS_FORMAT, ID_TWO, ID_FIFTEEN);
        assertCommandSuccess(enrolCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_enrolOneDogManyPrograms_success() {
        Set<Integer> dogIdSet = new HashSet<>();
        dogIdSet.add(ID_TWO);
        List<Integer> programIdList = Arrays.asList(ID_FIFTEEN, ID_SIXTEEN);
        Set<Integer> programIdSet = new HashSet<>(programIdList);

        Program editedProgram1 = (Program) expectedModel.getEntity(ID_FIFTEEN);
        Program editedProgram2 = (Program) expectedModel.getEntity(ID_SIXTEEN);

        HashSet<Integer> updatedEnrolledDogs1 = new HashSet<>(editedProgram1.getDogIdSet());
        updatedEnrolledDogs1.add(ID_TWO);

        expectedModel.setEntity(ID_FIFTEEN, new Program(editedProgram1.getName(),
                editedProgram1.getSessions(), editedProgram1.getTags(), updatedEnrolledDogs1));

        HashSet<Integer> updatedEnrolledDogs2 = new HashSet<>(editedProgram1.getDogIdSet());
        updatedEnrolledDogs2.add(ID_TWO);

        expectedModel.setEntity(ID_SIXTEEN, new Program(editedProgram2.getName(),
                editedProgram2.getSessions(), editedProgram2.getTags(), updatedEnrolledDogs2));

        expectedModel.updateFilteredEntityList(new IdMatchPredicate(programIdSet));

        EnrolCommand enrolCommand = new EnrolCommand(dogIdSet, programIdSet);
        String expectedMessage = String.format(EnrolCommand.MESSAGE_SUCCESS_FORMAT,
                ID_TWO,
                programIdSet.stream()
                        .map(String::valueOf)
                        .collect(Collectors.joining(", ")));

        assertCommandSuccess(enrolCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_enrolManyDogsOneProgram_success() {
        Set<Integer> programIdSet = new HashSet<>();
        List<Integer> dogIdList = Arrays.asList(ID_TWO, ID_FOUR, ID_SIX);
        Set<Integer> dogIdSet = new HashSet<>(dogIdList);
        programIdSet.add(ID_FIFTEEN);

        Program editedProgram = (Program) expectedModel.getEntity(ID_FIFTEEN);

        HashSet<Integer> updatedEnrolledDogs = new HashSet<>(editedProgram.getDogIdSet());
        updatedEnrolledDogs.addAll(dogIdSet);

        expectedModel.setEntity(ID_FIFTEEN, new Program(editedProgram.getName(),
                editedProgram.getSessions(), editedProgram.getTags(), updatedEnrolledDogs));

        expectedModel.updateFilteredEntityList(new IdMatchPredicate(programIdSet));

        EnrolCommand enrolCommand = new EnrolCommand(dogIdSet, programIdSet);
        String expectedMessage = String.format(EnrolCommand.MESSAGE_SUCCESS_FORMAT,
                dogIdSet.stream()
                        .map(String::valueOf)
                        .collect(Collectors.joining(", ")),
                ID_FIFTEEN);

        assertCommandSuccess(enrolCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_enrolManyDogsManyPrograms_failure() {
        List<Integer> dogIdList = Arrays.asList(ID_TWO, ID_FOUR, ID_SIX);
        List<Integer> programIdList = Arrays.asList(ID_FIFTEEN, ID_SIXTEEN);
        Set<Integer> dogIdSet = new HashSet<>(dogIdList);
        Set<Integer> programIdSet = new HashSet<>(programIdList);

        Program editedProgram1 = (Program) expectedModel.getEntity(ID_FIFTEEN);
        Program editedProgram2 = (Program) expectedModel.getEntity(ID_SIXTEEN);

        HashSet<Integer> updatedEnrolledDogs1 = new HashSet<>(editedProgram1.getDogIdSet());
        updatedEnrolledDogs1.add(ID_TWO);

        expectedModel.setEntity(ID_FIFTEEN, new Program(editedProgram1.getName(),
                editedProgram1.getSessions(), editedProgram1.getTags(), updatedEnrolledDogs1));

        HashSet<Integer> updatedEnrolledDogs2 = new HashSet<>(editedProgram1.getDogIdSet());
        updatedEnrolledDogs2.add(ID_TWO);

        expectedModel.setEntity(ID_SIXTEEN, new Program(editedProgram2.getName(),
                editedProgram2.getSessions(), editedProgram2.getTags(), updatedEnrolledDogs2));

        expectedModel.updateFilteredEntityList(new IdMatchPredicate(programIdSet));

        EnrolCommand enrolCommand = new EnrolCommand(dogIdSet, programIdSet);

        assertThrows(AssertionError.class, () -> enrolCommand.execute(model));
    }
}
