package company.server.service;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class BackupService {

    private static final String BACKUP_DIR = "C:/backup_postgres"; // путь, куда сохраняются бэкапы
    private static final String DB_NAME = "server_pass_company";
    private static final String DB_USER = "postgres";
    private static final String DB_PASSWORD = "postgres";
    private static final String DB_HOST = "localhost"; // если Java-клиент вне Docker
    private static final String PG_DUMP_PATH = "C:/Program Files/PostgreSQL/16/bin/pg_dump.exe";

    @Scheduled(cron = "0 0 3 1 * *")
    // раз в месяц, 1-го числа в 3:00
    public void createBackup() {
        String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        String fileName = "backup-" + date + ".sql";
        String backupPath = BACKUP_DIR + File.separator + fileName;

        ProcessBuilder pb =
                new ProcessBuilder(PG_DUMP_PATH, "-U", DB_USER, "-h", DB_HOST, "-p", "5432", "-f", backupPath, DB_NAME);

        pb.environment().put("PGPASSWORD", DB_PASSWORD);

        try {
            File dir = new File(BACKUP_DIR);
            if (!dir.exists()) {
                boolean created = dir.mkdirs();
                if (!created) {
                    log.error("Не удалось создать папку для бэкапов: {}", BACKUP_DIR);
                    return;
                }
            }

            Process process = pb.start();
            int exitCode = process.waitFor();

            if (exitCode == 0) {
                log.info("✅ Бэкап успешно создан: {}", backupPath);
            } else {
                log.error("❌ Ошибка при создании бэкапа. Код завершения: {}", exitCode);
            }

        } catch (IOException | InterruptedException e) {
            log.error("Исключение при создании бэкапа", e);
        }
    }
}
