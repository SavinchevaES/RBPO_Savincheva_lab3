ALTER TABLE volunteer_campaigns ADD CONSTRAINT ck_campaigns_dates CHECK (end_date >= start_date);

ALTER TABLE volunteer_tasks ADD CONSTRAINT ck_tasks_participants CHECK (required_participants >= 1);
ALTER TABLE volunteer_tasks ADD CONSTRAINT ck_tasks_duration CHECK (duration_hours >= 1);

ALTER TABLE participation_reports ADD CONSTRAINT ck_reports_hours CHECK (actual_hours >= 0);