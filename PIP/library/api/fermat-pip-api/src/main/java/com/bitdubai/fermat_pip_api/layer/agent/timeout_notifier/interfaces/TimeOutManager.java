package com.bitdubai.fermat_pip_api.layer.agent.timeout_notifier.interfaces;


import com.bitdubai.fermat_api.layer.actor.FermatActor;
import com.bitdubai.fermat_api.layer.all_definition.enums.AgentStatus;
import com.bitdubai.fermat_pip_api.layer.agent.timeout_notifier.exceptions.CantAddNewTimeOutAgentException;
import com.bitdubai.fermat_pip_api.layer.agent.timeout_notifier.exceptions.CantRemoveExistingTimeOutAgentException;
import com.bitdubai.fermat_pip_api.layer.agent.timeout_notifier.exceptions.CantResetTimeOutAgentException;
import com.bitdubai.fermat_pip_api.layer.agent.timeout_notifier.exceptions.CantStartTimeOutAgentException;
import com.bitdubai.fermat_pip_api.layer.agent.timeout_notifier.exceptions.CantStopTimeOutAgentException;

import java.util.List;
import java.util.UUID;

/**
 * Created by rodrigo on 3/26/16.
 * An agent that accepts subscriptions to start monitoring elapsed time and notify after a time out is detected.
 */
public interface TimeOutManager {

    /**
     * Adds a new Time Out Manager to the agent pools. Once added, further customization is possible.
     * The Agent can later be started, stopped or reset at will.
     * @param epochStartTime the Start time configured for this Agent.
     * @param timeout the elapsed time to monitor for a timeout in milliseconds
     * @param agentName the Name of the agent to be added.
     * @param owner a FermatActor that is the owner of the agent.
     * @return the newly created TimeOut Agent
     * @throws CantAddNewTimeOutAgentException
     */
    TimeOutAgent addNew(long epochStartTime, long timeout, String agentName, FermatActor owner) throws CantAddNewTimeOutAgentException;

    /**
     * Removes a configured Agent from the Manager. It is stopped if running.
     * After no longer in use, the agent should be removed.
     * @param timeOutAgent the Agent to be removed.
     * @throws CantRemoveExistingTimeOutAgentException
     */
    void remove(TimeOutAgent timeOutAgent) throws CantRemoveExistingTimeOutAgentException;

    /**
     * Starts the timeout Agent monitoring process.
     * @param timeOutAgent the Agent to start
     * @throws CantStartTimeOutAgentException
     */
    void startTimeOutAgent(TimeOutAgent timeOutAgent) throws CantStartTimeOutAgentException;

    /**
     * Stops the Timeout Agent. No monitoring is done, meaning that no notifications will be raised
     * at this state.
     * @param timeOutAgent the Agent to stop
     * @throws CantStopTimeOutAgentException
     */
    void stopTimeOutAgent(TimeOutAgent timeOutAgent) throws CantStopTimeOutAgentException;

    /**
     * Resets the counter of the agent if still running. A reset  means that the timeout duration counter
     * will start again. For example, if TimeOutDuration was 60 minutes and the reset occurs at minute 39,
     * another 60 minutes must passed for a new notification to be raised.
     * @param timeOutAgent the agent to reset
     * @throws CantResetTimeOutAgentException
     */
    void resetTimeOutAgent(TimeOutAgent timeOutAgent) throws CantResetTimeOutAgentException;

    /**
     * Gets the TimeoutAgent from the specified Id.
     * @param uuid the internal UUID of the Agent
     * @return an existing TimeOutAgent. Null if doesn't exists.
     */
    TimeOutAgent getTimeOutAgent(UUID uuid);

    /**
     * The List of all configured TimeOut Agents
     * @return all configured timeout agents or an empty list if no agents have been configured.
     */
    List<TimeOutAgent> getTimeOutAgents();

    /**
     * The list of all configured TimeOut Agents for the specified owner.
     * @param owner a Fermat actor that owns the agent
     * @return the list of configured TimeOutAgents owned for the specified owner. Empty list if none matched the criteria.
     */
    List<TimeOutAgent> getTimeOutAgents(FermatActor owner);

    /**
     * The list of all configured timeout agents by status.
     * @param status any possible status for the agents.
     * @return the agents matching the passed status. Empty list if none matched the criteria.
     */
    List<TimeOutAgent> getTimeOutAgents(AgentStatus status);

}
