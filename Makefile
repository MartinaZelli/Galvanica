include .env
$(eval export $(shell sed -ne 's/ *#.*$$//; /./ s/=.*$$// p' .env))

db-up:
	@mkdir -p /home/${USER}/${PROJECT_NAME}
	@docker-compose -f docker/docker-compose.yaml up -d db
	@echo "🚀 Database is up and running!"

db-down:
	@docker-compose -f docker/docker-compose.yaml down --volumes
	@echo " Database is down!"

db-drop: db-down
	@sudo rm -r /home/${USER}/${PROJECT_NAME}
	@echo " Database deleted!"
