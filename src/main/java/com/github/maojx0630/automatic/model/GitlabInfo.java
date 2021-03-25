package com.github.maojx0630.automatic.model;

import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @author 毛家兴
 * @date 2021-03-09 16:32
 */
@Data
public class GitlabInfo {
  private String author_name;
  private Date authored_date;
  private String committer_email;
  private Date committed_date;
  private Date created_at;
  private String author_email;
  private String id;
  private String short_id;
  private List<String> parent_ids;
  private String title;
  private String message;
  private String committer_name;
}
