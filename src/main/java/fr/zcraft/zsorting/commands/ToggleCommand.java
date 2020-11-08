package fr.zcraft.zsorting.commands;

import fr.zcraft.zlib.components.commands.CommandException;
import fr.zcraft.zlib.components.commands.CommandInfo;
import fr.zcraft.zlib.components.i18n.I;
import fr.zcraft.zsorting.ZSorting;
import fr.zcraft.zsorting.model.Bank;
import fr.zcraft.zsorting.tasks.SortingTask;

/**
 * Command triggered to select a bank.<br><br>
 * A selection is needed in order to manage inputs and outputs.
 * @author Lucas
 */
@CommandInfo (name = "toggle", usageParameters = "<name>")
public class ToggleCommand extends ZSortingCommands{
	
    @Override
    protected void run() throws CommandException {

        if (args.length < 1)
            throwInvalidArgument(I.t("A bank name is required."));

        Bank bank = ZSorting.getInstance().getBankManager().getNameToBank().get(args[0]);
        
        if(bank == null) {
            error(I.t("There is no bank with this name."));
        }
        else {
        	bank.setEnable(!bank.isEnable());
        	if(bank.isEnable()) {
            	bank.setToCompute(true);
            	if(!bank.hasInput())
            		warning(I.t("The bank does not have any input."));
            	if(!bank.hasOverflow())
            		warning(I.t("The bank does not have any overflow. Some items might clog up the inputs."));
            	SortingTask.getInstance().start();
            	success(I.t("The bank has been enabled."));
        	}
        	else
        		success(I.t("The bank has been disabled."));
        }
    }
}
