package org.betonquest.betonquest.conditions;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.Condition;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.exceptions.InstructionParseException;

/**
 * Checks if the variable value matches given pattern.
 */
@SuppressWarnings("PMD.CommentRequired")
public class VariableCondition extends Condition {

    private final String variable;
    private final String regex;

    public VariableCondition(final Instruction instruction) throws InstructionParseException {
        super(instruction, instruction.hasArgument("forceSync"));
        variable = instruction.next();
        regex = instruction.next().replace('_', ' ');
    }

    @Override
    protected Boolean execute(final Profile profile) {
        if (variable.charAt(0) == '%' && variable.endsWith("%")) {
            return BetonQuest.getInstance().getVariableValue(instruction.getPackage().getPackagePath(), variable, profile).matches(regex);
        } else {
            return variable.matches(regex);
        }
    }

}
