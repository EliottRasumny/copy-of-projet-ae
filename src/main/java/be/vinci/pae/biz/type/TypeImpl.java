package be.vinci.pae.biz.type;

public class TypeImpl implements Type {

  private int idType;
  private String label;

  @Override
  public int getIdType() {
    return idType;
  }

  @Override
  public void setIdType(int idType) {
    this.idType = idType;
  }

  @Override
  public String getLabel() {
    return label;
  }

  @Override
  public void setLabel(String label) {
    this.label = label;
  }

  @Override
  public String toString() {
    return "TypeImpl{"
        + "idType="
        + idType
        + ", label='"
        + label
        + '\''
        + '}';
  }
}
