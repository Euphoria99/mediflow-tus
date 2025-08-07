# Mediflow 

Patient Management application built with springboot and deployed in AWS


docker run --name dbhealth-postgres  -p 5432:5432  -e POSTGRES_DB=dbhealth -e POSTGRES_USER=admin_viewer  -e POSTGRES_PASSWORD=password  --network patient-net -d postgres

docker run --name patient-service -p 4000:4000 --network patient-net patientimg


docker run --name billing-service -p 4001:4001 -p 9001:9001 --network patient-net billingimg 