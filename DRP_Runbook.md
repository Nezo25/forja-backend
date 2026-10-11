# Disaster Recovery Plan (DRP) - Forja do Chico

1. **Isolamento**: Desative o Load Balancer redirecionando para página de manutenção.
2. **Provisionamento**: Suba nova instância EC2/Container com `docker-compose up -d`.
3. **Restauração**: Recupere o último dump gerado pelo `backup-cron.sh` do S3.
   ```bash
   zcat forja_db_YYYY-MM-DD.sql.gz | mysql -u root -p forja_db
   ```
4. **Retomada**: Atualize o apontamento de DNS e verifique o Health Check em `/actuator/health`.
