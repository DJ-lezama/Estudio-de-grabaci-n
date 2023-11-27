-- Procedimiento almacenado
DELIMITER //

CREATE PROCEDURE checkDuplicateAndOverlap(IN session_id VARCHAR(255), IN room_id VARCHAR(255), IN session_date DATE, IN session_start_time TIME, IN session_duration INT)
BEGIN
    DECLARE session_end_time TIME;
    DECLARE session_count INT;

    -- Calcular la hora de finalización sumando la duración a la hora de inicio
    SET session_end_time = ADDTIME(session_start_time, SEC_TO_TIME(session_duration * 3600));

    -- Redondear la hora de inicio y fin al bloque de horas más cercano
    SET session_start_time = TIME(DATE_FORMAT(session_start_time, '%H:00:00'));
    SET session_end_time = TIME(DATE_FORMAT(session_end_time, '%H:00:00'));

    -- Verificar la existencia de sesiones superpuestas
    SELECT COUNT(*)
    INTO session_count
    FROM sesiones
    WHERE idSesiones != session_id
      AND Sala_idSala = room_id
      AND fecha = session_date
      AND (
          (hora >= session_start_time AND hora < session_end_time)
          OR (hora <= session_start_time AND ADDTIME(hora, SEC_TO_TIME(duracion * 3600)) > session_start_time)
      );

    IF session_count > 0 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'No se puede crear la sesión. Existe una sesión que se superpone en la misma sala, en el mismo día y en el mismo bloque de horas.';
    END IF;
END //

DELIMITER ;

DELIMITER //

-- Trigger
CREATE TRIGGER before_insert_sesion
BEFORE INSERT ON sesiones
FOR EACH ROW
BEGIN
    CALL checkDuplicateAndOverlap(NEW.idSesiones, NEW.Sala_idSala, NEW.fecha, NEW.hora, NEW.duracion);
END //

DELIMITER ;
