package mona;

import java.time.LocalDate;

/**
 * Shows tasks that occur a specified number of days from today.
 */
public class InCommand extends Command {
    private final int daysAhead;

    /**
     * Creates a command that shows tasks occurring the given number of days from today.
     *
     * @param daysAhead the non-negative number of days to look ahead.
     */
    public InCommand(int daysAhead) {
        this.daysAhead = daysAhead;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        LocalDate targetDate = LocalDate.now().plusDays(daysAhead);
        showMatchingTasks(tasks, targetDate,
                "✨ In " + daysAhead + " day(s), the stars reveal:", ui);
    }
}
