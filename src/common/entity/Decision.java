package common.entity;

import java.time.ZonedDateTime;

public class Decision extends Stage {

	public Decision(ChangeRequest request, StageName stageName, ZonedDateTime startTime, ZonedDateTime deadline,
			ITEngineer executionEngineer, boolean canBeExtend, boolean delay) {
		super(request, stageName, startTime, deadline, executionEngineer, canBeExtend, delay);
		// TODO Auto-generated constructor stub
	}

	@Override
	public int result() {
		// TODO Auto-generated method stub
		return 0;
	}

}
